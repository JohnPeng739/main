package org.mx.tools.ffee.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FamilyMember;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.FamilyRepository;
import org.mx.tools.ffee.service.FamilyService;
import org.mx.tools.ffee.service.FileTransportService;
import org.mx.tools.ffee.service.bean.FamilyInfoBean;
import org.mx.tools.ffee.service.bean.FamilyMemberInfoBean;
import org.mx.tools.ffee.utils.QrCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component("familyService")
public class FamilyServiceImpl implements FamilyService {
    private static final Log logger = LogFactory.getLog(FamilyServiceImpl.class);

    private GeneralAccessor generalAccessor;
    private FamilyRepository familyRepository;
    private FileTransportService fileTransportService;

    @Autowired
    public FamilyServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor,
                             FamilyRepository familyRepository, FileTransportService fileTransportService) {
        super();
        this.generalAccessor = generalAccessor;
        this.familyRepository = familyRepository;
        this.fileTransportService = fileTransportService;
    }

    @Transactional
    @Override
    public Family saveFamily(FamilyInfoBean familyInfoBean) {
        if (familyInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The family info is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String familyId = familyInfoBean.getId();
        Family family = null;
        if (!StringUtils.isBlank(familyId)) {
            family = generalAccessor.getById(familyId, Family.class);
        }
        if (family == null) {
            // 建立新家庭
            family = EntityFactory.createEntity(Family.class);
        }
        family.setAvatarUrl(familyInfoBean.getAvatarUrl());
        family.setDesc(familyInfoBean.getDesc());
        family.setName(familyInfoBean.getName());
        family = generalAccessor.save(family);
        return saveFamilyMember(family, familyInfoBean.getMembers());
    }

    @Transactional(readOnly = true)
    @Override
    public Family getFamily(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return generalAccessor.getById(familyId, Family.class);
    }

    private Family saveFamilyMember(Family family, List<FamilyMemberInfoBean> members) {
        if (members == null || members.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("The family member list is null or empty.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
            );
        }
        for (FamilyMemberInfoBean member : members) {
            String id = member.getId(),
                    role = member.getRole(),
                    accountId = member.getAccountId();
            boolean isOwner = member.isOwner();
            if (StringUtils.isBlank(accountId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The family member's account id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
            if (account == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The account[%s] not found.", accountId));
                }
            }
            FamilyMember familyMember = null;
            if (!StringUtils.isBlank(id)) {
                familyMember = generalAccessor.getById(id, FamilyMember.class);
            }
            if (familyMember == null) {
                familyMember = EntityFactory.createEntity(FamilyMember.class);
            }
            familyMember.setRole(role);
            familyMember.setFamily(family);
            familyMember.setAccount(account);
            familyMember.setIsOwner(isOwner);
            generalAccessor.save(familyMember);
        }
        return getFamily(family.getId());
    }

    @Transactional
    @Override
    public Family joinFamily(FamilyInfoBean familyInfoBean) {
        if (familyInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The family info is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String familyId = familyInfoBean.getId();
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        return saveFamilyMember(family, familyInfoBean.getMembers());
    }

    @Override
    public List<AccessLog> getAccessLogsByFamilyId(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return familyRepository.findAccessLogByFamilyId(familyId);
    }

    private Family getFamilyByOpenId(String openId) {
        if (StringUtils.isBlank(openId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's open id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String familyId = familyRepository.findFamilyIdByOpenId(openId);
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family not found, open id: %s.", openId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        } else {
            return getFamily(familyId);
        }
    }

    @Transactional
    @Override
    public String changeFamilyAvatar(String familyId, InputStream in) {
        Family family = getFamily(familyId);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        String root = System.getProperty("user.dir");
        Path path = Paths.get(String.format("%s/family/avatar-%s.png", root, familyId));
        String filePath = fileTransportService.uploadFile(path.toString(), in);
        family.setAvatarUrl(filePath.substring(root.length()));
        family = generalAccessor.save(family);
        return family.getAvatarUrl();
    }

    @Transactional(readOnly = true)
    @Override
    public File getFamilyAvatar(String familyId) {
        Family family = getFamily(familyId);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        Path path = Paths.get(String.format("%s/family/avatar-%s.png", System.getProperty("user.dir"), familyId));
        return fileTransportService.downloadFile(path.toString());
    }

    @Transactional(readOnly = true)
    @Override
    public File getFamilyQrCode(String familyId) {
        Family family = getFamily(familyId);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        JSONObject json = new JSONObject();
        json.put("id", family.getId());
        json.put("name", family.getName());
        try {
            Path path = Paths.get(String.format("%s/resources/qrcodes/%s.png", System.getProperty("user.dir"), familyId));
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            QrCodeUtils.createQrCode(300, 300, json.toJSONString(), path.toString());
            return fileTransportService.downloadFile(path.toString());
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Create a temple file fail.", ex);
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_IO_FAIL
            );
        }
    }
}
