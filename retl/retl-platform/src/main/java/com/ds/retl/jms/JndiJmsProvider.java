/**
 * 
 */
package com.ds.retl.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsProvider;
import org.mx.StringUtils;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ResourceAllocationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 用于由容器提供的JMS功能的Provider，使用通用的JNDI方式连接MQ服务器。
 * 
 * @author Josh Peng（彭明喜）<br>
 *         <b>修改历史</b>：<br>
 *         &nbsp;&nbsp; 2014-12-19 初稿<br>
 * 
 */
@SuppressWarnings("serial")
public class JndiJmsProvider implements JmsProvider {
	private static final Log logger = LogFactory.getLog(JndiJmsProvider.class);
	private ConnectionFactory connectionFactory = null;
	private Destination destination = null;

	/**
	 * 默认的构造函数
	 * 
	 * @param connectionFactoryName
	 *            连接MQ服务器的连接工厂名字，配置在JNDI中。
	 * @param destinateName
	 *            连接MQ服务器上的队列（<code>Queue</code>）或者主题（<code>Topic</code>
	 *            ）的名字，配置在JNDI中。
	 * @throws JMSException
	 *             初始化JMS连接代理发生的异常。
	 */
	public JndiJmsProvider(String connectionFactoryName, String destinateName)
			throws JMSException {
		if (StringUtils.isBlank(connectionFactoryName)) {
			String msg = "The ConnectionFactoryName is blank.";
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new ResourceAllocationException(msg);
		}
		if (StringUtils.isBlank(destinateName)) {
			String msg = "The DestinateName is blank.";
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			throw new ResourceAllocationException(msg);
		}
		try {
			InitialContext context = new InitialContext();
			// 获取QueueConnectionFactory对象
			connectionFactory = (ConnectionFactory) context
					.lookup(connectionFactoryName);
			// 获取Destination对象
			destination = (Destination) context.lookup(destinateName);
			if (logger.isErrorEnabled()) {
				logger.debug(String.format("Initialize JndiJmsProvider success，ConnectionFactoryName: %s, DestinateName: %s.",
						connectionFactoryName, destinateName));
			}
		} catch (NamingException ex) {
			String msg = String.format("Initialize JndiJmsProvider fail，ConnectionFactoryName: %s, DestinateName: %s.",
					connectionFactoryName, destinateName);
			if (logger.isErrorEnabled()) {
				logger.error(msg, ex);
			}
			throw new ResourceAllocationException(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.contrib.jms.JmsProvider#connectionFactory()
	 */
	@Override
	public ConnectionFactory connectionFactory() throws Exception {
		return connectionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.contrib.jms.JmsProvider#destination()
	 */
	@Override
	public Destination destination() throws Exception {
		return destination;
	}

}
