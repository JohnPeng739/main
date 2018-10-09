package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.service.bean.FamilyInfoBean;
import org.mx.tools.ffee.service.bean.FamilyMemberInfoBean;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface FamilyService {
    Family saveFamily(FamilyInfoBean familyInfoBean);

    Family getFamily(String familyId);

    Family saveFamilyMember(FamilyMemberInfoBean familyMemberInfoBean);

    List<AccessLog> getAccessLogsByFamilyId(String familyId);

    String changeFamilyAvatar(String familyId, InputStream in);

    File getFamilyAvatar(String familyId);

    File getFamilyQrCode(String familyId);
}
