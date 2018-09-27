package org.mx.tools.ffee.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FamilyMember;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.FamilyRepository;
import org.mx.tools.ffee.service.FamilyService;
import org.mx.tools.ffee.service.FileTransportService;
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
    public Family createFamily(Family family, String openId, String ownerRole) {
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        FfeeAccount owner = generalAccessor.findOne(GeneralAccessor.ConditionTuple.eq("openId", openId),
                FfeeAccount.class);
        if (owner == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", openId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        // 采用家庭名称来判定是否存在
        if (!StringUtils.isBlank(family.getName())) {
            Family checked = generalAccessor.findOne(
                    GeneralAccessor.ConditionTuple.eq("name", family.getName()),
                    Family.class);
            if (checked != null) {
                // 家庭已经存在
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The family[%s] has existed.", family.getName()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
                );
            }
        }
        // 新增家庭
        FamilyMember member = EntityFactory.createEntity(FamilyMember.class);
        member.setFfeeAccount(owner);
        member.setRole(ownerRole);
        member.setIsOwner(true);
        member = generalAccessor.save(member);

        family.setId(null);
        family.getMembers().add(member);
        family = generalAccessor.save(family);
        if (logger.isDebugEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Create a new family successfully, id: %s, name: %s.",
                        family.getId(), family.getName()));
            }
        }
        return family;
    }

    @Transactional
    @Override
    public Family modifyFamily(Family family) {
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (!StringUtils.isBlank(family.getId())) {
            Family checked = generalAccessor.getById(family.getId(), Family.class);
            if (checked != null) {
                // 修改家庭
                checked.setAvatarUrl(family.getAvatarUrl());
                checked.setDesc(family.getDesc());
                checked.setName(family.getName());
                return generalAccessor.save(checked);
            }
        }
        if (logger.isErrorEnabled()) {
            logger.error(String.format("The family[%s] not found.", family.getId()));
        }
        throw new UserInterfaceFfeeErrorException(
                UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
        );
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

    @Transactional
    @Override
    public Family joinFamily(String familyId, String role, String accountId) {
        if (StringUtils.isBlank(familyId) || StringUtils.isBlank(accountId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id or account id is blank.");
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
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        for (FamilyMember member : family.getMembers()) {
            if (member.getFfeeAccount().getId().equals(account.getId())) {
                // 已经是家庭成员了，忽略
                return family;
            }
        }
        FamilyMember member = EntityFactory.createEntity(FamilyMember.class);
        member.setFfeeAccount(account);
        member.setRole(role);
        member.setIsOwner(false);
        member = generalAccessor.save(member);
        family.getMembers().add(member);
        family = generalAccessor.save(family);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The account[%s] join the family[%s] successfully.", accountId, familyId));
        }
        return family;
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
