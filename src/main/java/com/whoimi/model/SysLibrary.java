package com.whoimi.model;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author whoimi 
 * @since 2024-08-21T16:24:35.328725700 
 */

@Table (name ="SYS_LIBRARY",schema = "PCL" )
public class SysLibrary  implements Serializable {

    @Serial
	private static final long serialVersionUID =  1600186253192388222L;

	/**
	 * 文件编号
	 */
	@Id
   	@Column("LIB_ID")
	private String libId;

	/**
	 * 文件创建时间
	 */
   	@Column("LIB_CREATE_TIME")
	private LocalDate libCreateTime;

	/**
	 * 文件名称
	 */
   	@Column("LIB_NAME")
	private String libName;

	/**
	 * 标题
	 */
   	@Column("LIB_TITLE")
	private String libTitle;

	/**
	 * 目录
	 */
   	@Column("LIB_DIRECTORY")
	private String libDirectory;

	/**
	 * 后缀名
	 */
   	@Column("LIB_EXT_NAME")
	private String libExtName;

	/**
	 * 类型
	 */
   	@Column("LIB_TYPE")
	private BigDecimal libType;

	/**
	 * 状态
	 */
   	@Column("STATE")
	private BigDecimal state;

	/**
	 * 大小
	 */
   	@Column("LIB_SIZE")
	private BigDecimal libSize;

	/**
	 * 描述
	 */
   	@Column("LIB_DESC")
	private String libDesc;

	/**
	 * 创建人
	 */
   	@Column("LIB_OWNER_NAME")
	private String libOwnerName;

	/**
	 * 创建人姓名
	 */
   	@Column("LIB_OWNER_ID")
	private String libOwnerId;

	/**
	 * 图片宽度
	 */
   	@Column("LIB_WIDTH")
	private String libWidth;

	/**
	 * 图片高度
	 */
   	@Column("LIB_HEIGHT")
	private String libHeight;

	/**
	 * 缩略图
	 */
   	@Column("LIB_MINI")
	private String libMini;

	/**
	 * 页面
	 */
   	@Column("LIB_PAGE")
	private String libPage;

	/**
	 * 角色编号
	 */
   	@Column("LIB_ROLE_ID")
	private String libRoleId;

	/**
	 * 所属菜单
	 */
   	@Column("MENU_ID")
	private String menuId;

   	@Column("URL_TYPE")
	private BigDecimal urlType;

	public String getLibId() {
		return this.libId;
	}

	public void setLibId(String libId) {
		this.libId = libId;
	}

	public java.time.LocalDate getLibCreateTime() {
		return this.libCreateTime;
	}

	public void setLibCreateTime(java.time.LocalDate libCreateTime) {
		this.libCreateTime = libCreateTime;
	}

	public String getLibName() {
		return this.libName;
	}

	public void setLibName(String libName) {
		this.libName = libName;
	}

	public String getLibTitle() {
		return this.libTitle;
	}

	public void setLibTitle(String libTitle) {
		this.libTitle = libTitle;
	}

	public String getLibDirectory() {
		return this.libDirectory;
	}

	public void setLibDirectory(String libDirectory) {
		this.libDirectory = libDirectory;
	}

	public String getLibExtName() {
		return this.libExtName;
	}

	public void setLibExtName(String libExtName) {
		this.libExtName = libExtName;
	}

	public java.math.BigDecimal getLibType() {
		return this.libType;
	}

	public void setLibType(java.math.BigDecimal libType) {
		this.libType = libType;
	}

	public java.math.BigDecimal getState() {
		return this.state;
	}

	public void setState(java.math.BigDecimal state) {
		this.state = state;
	}

	public java.math.BigDecimal getLibSize() {
		return this.libSize;
	}

	public void setLibSize(java.math.BigDecimal libSize) {
		this.libSize = libSize;
	}

	public String getLibDesc() {
		return this.libDesc;
	}

	public void setLibDesc(String libDesc) {
		this.libDesc = libDesc;
	}

	public String getLibOwnerName() {
		return this.libOwnerName;
	}

	public void setLibOwnerName(String libOwnerName) {
		this.libOwnerName = libOwnerName;
	}

	public String getLibOwnerId() {
		return this.libOwnerId;
	}

	public void setLibOwnerId(String libOwnerId) {
		this.libOwnerId = libOwnerId;
	}

	public String getLibWidth() {
		return this.libWidth;
	}

	public void setLibWidth(String libWidth) {
		this.libWidth = libWidth;
	}

	public String getLibHeight() {
		return this.libHeight;
	}

	public void setLibHeight(String libHeight) {
		this.libHeight = libHeight;
	}

	public String getLibMini() {
		return this.libMini;
	}

	public void setLibMini(String libMini) {
		this.libMini = libMini;
	}

	public String getLibPage() {
		return this.libPage;
	}

	public void setLibPage(String libPage) {
		this.libPage = libPage;
	}

	public String getLibRoleId() {
		return this.libRoleId;
	}

	public void setLibRoleId(String libRoleId) {
		this.libRoleId = libRoleId;
	}

	public String getMenuId() {
		return this.menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public java.math.BigDecimal getUrlType() {
		return this.urlType;
	}

	public void setUrlType(java.math.BigDecimal urlType) {
		this.urlType = urlType;
	}

}
