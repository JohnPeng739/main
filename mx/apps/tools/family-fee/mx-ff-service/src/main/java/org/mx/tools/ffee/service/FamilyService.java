package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;

public interface FamilyService {
    Family saveFamily(Family family);
    Family getFamily(String familyId);
    Family joinFamily(String familyId, String openId);
}
