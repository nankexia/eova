/**
 * Copyright (c) 2013-2016, Jieven. All rights reserved.
 *
 * Licensed under the GPL license: http://www.gnu.org/licenses/gpl.txt
 * To use it on other terms please contact us at 1623736450@qq.com
 */
package com.oss;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.eova.config.EovaConfig;
import com.eova.interceptor.LoginInterceptor;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.mysql.jdbc.Connection;
import com.oss.model.Hotel;
import com.oss.model.OrderItem;
import com.oss.model.Orders;
import com.oss.model.Product;
import com.oss.product.ProductController;

public class OSSConfig extends EovaConfig {

	/**
	 * 自定义路由
	 * 
	 * @param me
	 */
	@Override
	protected void route(Routes me) {
		// 自定义的路由配置往这里加。。。
		me.add("/product", ProductController.class);

		// 不需要登录拦截的URL
		LoginInterceptor.excludes.add("/init");
	}

	/**
	 * 自定义Main数据源Model映射
	 * 
	 * @param arp
	 */
	@Override
	protected void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("hotel", Hotel.class);
		arp.addMapping("product", Product.class);
		arp.addMapping("orders", Orders.class);
		arp.addMapping("order_item", OrderItem.class);
		// 自定义的Model映射往这里加。。。
	}

	/**
	 * 自定义插件
	 */
	@Override
	protected void plugin(Plugins plugins) {
		// 添加数据源
		// 数据源Key
		String datasource = "bar";

		// 添加数据源
		String ossUrl, ossUser, ossPwd;
		ossUrl = props.get("bar_url");
		ossUser = props.get("bar_user");
		ossPwd = props.get("bar_pwd");

		WallFilter wall = new WallFilter();
		wall.setDbType(JdbcUtils.MYSQL);

		DruidPlugin dp = new DruidPlugin(ossUrl, ossUser, ossPwd);
		dp.addFilter(new StatFilter());
		dp.addFilter(wall);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(datasource, dp);
		// 方言
		arp.setDialect(new MysqlDialect());
		// 事务级别
		arp.setTransactionLevel(Connection.TRANSACTION_REPEATABLE_READ);
		// 统一全部默认小写
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		// 是否显示SQL
		arp.setShowSql(true);
		System.out.println("load data source:" + ossUrl + "/" + ossUser);

		// arp.addMapping("xxx", Xxx.class);
		plugins.add(dp).add(arp);

		// 注册数据源
		dataSources.add(datasource);
		// 添加自动扫描插件

		// ...
	}

	/**
	 * Run Server
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFinal.start("webapp", 80, "/", 0);
	}

}