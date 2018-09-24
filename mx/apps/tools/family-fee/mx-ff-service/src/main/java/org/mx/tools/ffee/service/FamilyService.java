package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;

public interface FamilyService {
    Family createFamily(Family family, String openId, String ownerRole);

    Family modifyFamily(Family family);

    Family getFamily(String familyId);

    Family joinFamily(String familyId, String role, String accountId);
}
