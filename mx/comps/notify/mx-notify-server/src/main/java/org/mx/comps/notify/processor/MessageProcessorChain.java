package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 通知消息服务处理器链
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Component("messageProcessorChain")
public class MessageProcessorChain {
    private static final Log logger = LogFactory.getLog(MessageProcessorChain.class);

    private Set<MessageProcessor> processors;

    /**
     * 默认的构造函数
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     */
    @Autowired
    public MessageProcessorChain(Environment env, ApplicationContext context) {
        super();
        this.processors = new HashSet<>();
        String names = env.getProperty("websocket.notify.processors");
        for (String name : StringUtils.split(names)) {
            String processorName = String.format("%sCommandProcessor", name);
            try {
                MessageProcessor processor = context.getBean(processorName, MessageProcessor.class);
                this.processors.add(processor);
            } catch (BeansException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The message processor[%s] not found.", processorName));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
            }
        }
        if (processors.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There is none message processor.");
            }
        }
    }

    /**
     * 添加一个JSON消息处理器到处理链中
     *
     * @param processor JSON消息处理器
     */
    public void addProcessor(MessageProcessor processor) {
        if (processor != null) {
            processors.add(processor);
        }
    }

    /**
     * 处理收到的一条JSON消息
     *
     * @param connectKey 连接关键字
     * @param json       消息
     */
    public void processJsonCommand(final String connectKey, final JSONObject json) {
        if (json == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The JSON command is null.");
            }
            return;
        }
        if (processors == null || processors.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There has not any command processors.");
            }
            return;
        }
        Session session = WsSessionManager.getManager().getSession(connectKey);
        if (session == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The session[%s] not existed.", connectKey));
            }
        }
        for (MessageProcessor processor : processors) {
            if (processor.processJsonCommand(session, json)) {
                break;
            }
        }
    }

    /**
     * 处理收到的流式数据
     *
     * @param connectKey 连接关键字
     * @param buffer     二进制数据
     */
    public void processBinaryData(final String connectKey, final byte[] buffer) {
        if (buffer == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The binary data is null.");
            }
            return;
        }
        if (processors == null || !processors.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There has not any command processors.");
            }
            return;
        }
        Session session = WsSessionManager.getManager().getSession(connectKey);
        if (session == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The session[%s] not existed.", connectKey));
            }
        }
        for (MessageProcessor processor : processors) {
            if (processor.processBinaryData(session, buffer)) {
                return;
            }
        }
    }
}
