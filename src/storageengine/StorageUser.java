package storageengine;

import java.util.ArrayList;

public class StorageUser {

	public static enum Permission {
		CREATE_TABLES, DELETE_TABLES, MODIFY_TABLE
	}

	private String username, password, salt;
	private ArrayList<String> tableNameAccess;
	private ArrayList<Permission> permissions = new ArrayList<Permission>() {
		private static final long serialVersionUID = 1L;
		{
			add(Permission.MODIFY_TABLE);
		}
	};

	public StorageUser(String username, String password, ArrayList<Permission> grantPermissions) {
		this.username = username;
		this.salt = Utils.randomString(30);
		this.password = Utils.md5(password + salt);
		this.tableNameAccess = new ArrayList<String>();
		if (grantPermissions != null)
			permissions.addAll(grantPermissions);
	}

	public String getUsername() {
		return username;
	}

	public ArrayList<String> getTableNameAccess() {
		return tableNameAccess;
	}

	public void grantTableAccess(String tableName) {
		tableNameAccess.add(tableName);
	}

	public void removeTableAccess(String tableName) {
		tableNameAccess.remove(tableName);
	}

	public boolean checkCredentials(String username, String password) {
		return username.equals(this.username) && Utils.md5(password + salt).equals(this.password);
	}
}
