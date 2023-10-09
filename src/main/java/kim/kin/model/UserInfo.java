package kim.kin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


/**
 * @author choky
 */
@Table("kim_user_info")
public class UserInfo {

	@Id
	private long id;
	@Column("username")
	private String username;
	@Column("password")
	@JsonIgnore
	private String password;
	@Column("enabled")
	private Boolean enabled=true;

	@Column("avatar")
	private String avatar;
	@Column("introduction")
	private String introduction;

	@Column("email")
	private String email;
	@Column("mobile")
	private String mobile;
	@Column("gender")
	private String gender;

	@Column("desc")
	private String desc;
	@Column("home_path")
	private String homePath;
	@Column("real_name")
	private String realName;


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getHomePath() {
		return homePath;
	}

	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public UserInfo(long id, String username, String password, Boolean enabled, String avatar, String introduction, String email, String mobile, String gender, String desc, String homePath, String realName) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.avatar = avatar;
		this.introduction = introduction;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.desc = desc;
		this.homePath = homePath;
		this.realName = realName;
	}

	public UserInfo() {
	}
}
