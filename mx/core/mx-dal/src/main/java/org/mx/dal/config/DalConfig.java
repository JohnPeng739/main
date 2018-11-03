package org.mx.dal.config;

import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.Import;

/**
 * DAL（数据访问层）Java Configure类
 * <p>
 * 载入：org.mx.dal.session.impl中的组件
 *
 * @author : john.peng date : 2017/10/7
 */
@Import({SpringConfig.class})
public class DalConfig {
}
