package com.quotamanagesys.interceptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Session;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultCompany;
import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.DepartmentDao;
import com.quotamanagesys.dao.UserDao;
import com.quotamanagesys.dao.UserDeptDao;

@Component
public class UserInfoImportCore extends HibernateDao {
	
	@Resource
	UserDao userDao;
	@Resource
	UserDeptDao userDeptDao;
	@Resource
	DepartmentDao departmentDao;
	
	@Expose
	public void insertOrUpdateUserInfo() throws SQLException{
		Connection conn=getDBConnection();
		ResultSet rs=null;
		boolean isSuccess=true;

		try {
			rs=getResultSet(conn,"select * from user_basic_info_update");
		} catch (Exception e) {
			isSuccess=false;
		}
		
		if (isSuccess) {
			Session session=this.getSessionFactory().openSession();
			
			while (rs.next()) {
				String username=rs.getString("username");
				DefaultUser user=userDao.getUser(username);
				if (user==null) {
					user=new DefaultUser();
					user.setUsername(username);
					user.setCname(rs.getString("cname"));
					user.setEname(rs.getString("ename"));	
					//ȡֵ��ΧΪ���С�����Ů����null
					if ((rs.getString("male")).equals("��")) {
						user.setMale(true);
					}else {
						user.setMale(false);
					}	
					//ȡֵ��ΧΪ���ǡ�������
					if ((rs.getString("administrator")).equals("��")) {
						user.setAdministrator(true);
					} else {
						user.setAdministrator(false);
					}
					user.setMobile(rs.getString("mobile"));
					user.setEmail(rs.getString("email"));
					//ȡֵ��ΧΪ���ǡ�������
					if ((rs.getString("enable")).equals("��")) {
						user.setEnabled(true);
					} else {
						user.setEnabled(false);
					}
					//ȡֵ��ΧΪ�������� 
					user.setBirthday(rs.getDate("birthday"));
					user.setAddress(rs.getString("address"));
					
					String salt = String.valueOf(RandomUtils.nextInt(100));//������
					ShaPasswordEncoder passwordEncoder=new ShaPasswordEncoder();//BDF2��ܼ��ܷ�ʽΪShaPasswordEncoder
					String password = passwordEncoder.encodePassword("11111111", salt);// ������Ѽ��ܵ�����
					user.setSalt(salt);
					user.setPassword(password);
					//Ĭ�ϵ�companyIdΪ"hcg"
					user.setCompanyId("hcg");
					session.save(user);
					session.flush();
					session.clear();
					
					String departmentname=rs.getString("departmentname");
					DefaultDept dept=departmentDao.getDeptByName(departmentname);
					if (dept!=null) {
						userDeptDao.addUserDept(username, dept.getId());
					}
				} else {
					user.setCname(rs.getString("cname"));
					user.setEname(rs.getString("ename"));	
					//ȡֵ��ΧΪ���С�����Ů����null
					if ((rs.getString("male")).equals("��")) {
						user.setMale(true);
					}else {
						user.setMale(false);
					}	
					//ȡֵ��ΧΪ���ǡ�������
					if ((rs.getString("administrator")).equals("��")) {
						user.setAdministrator(true);
					} else {
						user.setAdministrator(false);
					}
					user.setMobile(rs.getString("mobile"));
					user.setEmail(rs.getString("email"));
					//ȡֵ��ΧΪ���ǡ�������
					if ((rs.getString("enable")).equals("��")) {
						user.setEnabled(true);
					} else {
						user.setEnabled(false);
					}
					//ȡֵ��ΧΪ�������� 
					user.setBirthday(rs.getDate("birthday"));
					user.setAddress(rs.getString("address"));
					session.merge(user);
					session.flush();
					session.clear();
					
					String departmentname=rs.getString("departmentname");
					DefaultDept dept=departmentDao.getDeptByName(departmentname);
					if (dept!=null) {
						userDeptDao.changeUserDept(username, dept.getId());
					}
				}
				
				String clearThisRecord="DELETE FROM user_basic_info_update WHERE username='"+username+"'";
				excuteSQL(clearThisRecord);
			}
			session.close();
		}
		conn.close();
	}
	
	public void excuteSQL(String SQL) {
		Session session = this.getSessionFactory().openSession();
		try {
			session.createSQLQuery(SQL).executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	public ResultSet getResultSet(Connection conn,String sql) throws SQLException{
		PreparedStatement statement=conn.prepareStatement(sql);
		ResultSet rs=statement.executeQuery();
		return rs;
	}
	
	public Connection getDBConnection(){
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/quotamanagesysdb?useUnicode=true&amp;characterEncoding=UTF-8";
		String user = "root"; 
		String password = "abcd1234";
		try { 
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
	     }catch(Exception e){
	    	System.out.print(e.toString());
	    	return null;
	     }
	}
}
