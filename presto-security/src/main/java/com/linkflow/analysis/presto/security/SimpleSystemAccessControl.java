package com.linkflow.analysis.presto.security;

import com.facebook.airlift.log.Logger;
import com.facebook.presto.common.CatalogSchemaName;
import com.facebook.presto.spi.CatalogSchemaTableName;
import com.facebook.presto.spi.ColumnMetadata;
import com.facebook.presto.spi.SchemaTableName;
import com.facebook.presto.spi.security.*;


import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.security.Principal;
import java.util.Optional;

import static com.facebook.presto.spi.security.AccessDeniedException.denyShowSchemas;

public class SimpleSystemAccessControl implements SystemAccessControl {
    private final Logger logger = Logger.get(SimpleSystemAccessControl.class);
    final Set<String> ADMIN = new HashSet<>();

    final Set<String> HUECatalog = new HashSet<>();

    final Set<String> HUETables = new HashSet<>();

    final Set<String> ALLUSERS = new HashSet<>();

    final Set<String> HUEUSERS = new HashSet<>();


    public SimpleSystemAccessControl(Map<String, String> config) {
        this.ADMIN.add("root");
        this.HUECatalog.add("hive");
        this.HUETables.add("event_vd");
        this.HUETables.add("profile_vd");
        this.ALLUSERS.addAll(this.ADMIN);
    }

    /**
     * 校验user是否存在，所有的命令都会先走这个校验，所以这里一定要配置完善
     *
     * @param context
     * @param principal
     * @param userName
     */
    @Override
    public void checkCanSetUser(AccessControlContext context, Optional<Principal> principal, String userName) {
        logger.warn("call checkCanSetUser...");
        Map<String, String> userInfo = (SecurityUtil.getInstance()).userInfo;
        if (!userInfo.containsKey(userName.toLowerCase()))
            AccessDeniedException.denySetUser(principal, userName);
    }

    @Override
    public void checkQueryIntegrity(Identity identity, AccessControlContext context, String query) {
        logger.warn("call checkQueryIntegrity...");
    }

    /**
     * 所有在配置文件中的用户都可以set session property
     *
     * @param identity
     * @param context
     * @param propertyName
     */
    @Override
    public void checkCanSetSystemSessionProperty(Identity identity, AccessControlContext context, String propertyName) {
        logger.warn("call checkCanSetSystemSessionProperty...");
        Map<String, String> userInfo = (SecurityUtil.getInstance()).userInfo;
        if (!userInfo.containsKey(identity.getUser().toLowerCase()))
            AccessDeniedException.denySetSystemSessionProperty(propertyName);
    }

    /**
     * 非root用户不能 show schemas
     *
     * @param identity
     * @param context
     * @param catalogName
     */
    @Override
    public void checkCanShowSchemas(Identity identity, AccessControlContext context, String catalogName) {
        logger.warn("call checkCanShowSchemas...");
        String user = identity.getUser().toLowerCase();
        if (!this.ADMIN.contains(user))
            AccessDeniedException.denyShowSchemas("user : " + identity.getUser() + " do not have permission");
    }

    @Override
    public Set<String> filterCatalogs(Identity identity, AccessControlContext context, Set<String> catalogs) {
        logger.warn("call filterCatalogs...");
        return catalogs;
    }

    /**
     * 只有在配置文件中的user 才能访问对应的 catalog
     *
     * @param identity
     * @param context
     * @param catalogName
     */
    @Override
    public void checkCanAccessCatalog(Identity identity, AccessControlContext context, String catalogName) {
        logger.warn("call checkCanAccessCatalog...");
        Map<String, String> userInfo = (SecurityUtil.getInstance()).userInfo;
        String currentUser = identity.getUser().toLowerCase();
        if (!this.ADMIN.contains(currentUser)) {//如果当前用户不是root
            if (!userInfo.containsKey(currentUser) || !this.HUECatalog.contains(catalogName))
                AccessDeniedException.denyCatalogAccess(catalogName);
        }
    }

    /**
     * 只有root可以 create schema
     *
     * @param identity
     * @param context
     * @param schema
     */
    @Override
    public void checkCanCreateSchema(Identity identity, AccessControlContext context, CatalogSchemaName schema) {
        logger.warn("call checkCanCreateSchema...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyCreateSchema(schema.toString());
    }

    /**
     * 只有root可以 drop schema
     *
     * @param identity
     * @param context
     * @param schema
     */
    @Override
    public void checkCanDropSchema(Identity identity, AccessControlContext context, CatalogSchemaName schema) {
        logger.warn("call checkCanDropSchema...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyDropSchema(schema.toString());
    }

    /**
     * 只有root可以 rename schema
     *
     * @param identity
     * @param context
     * @param schema
     */
    @Override
    public void checkCanRenameSchema(Identity identity, AccessControlContext context, CatalogSchemaName schema, String newSchemaName) {
        logger.warn("call checkCanRenameSchema...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyRenameSchema(schema.toString(), newSchemaName);
    }

    /**
     * show schemas
     * 如果 presto-access-schemas.properties 有配置的 schemas 就可以看到
     * 如果没有配置，默认show和user(tenant_id)一致的schema(user:362 -> schema:dw_t362)
     *
     * @param identity
     * @param context
     * @param catalogName
     * @param schemaNames
     * @return
     */
    @Override
    public Set<String> filterSchemas(Identity identity, AccessControlContext context, String catalogName, Set<String> schemaNames) {
        logger.warn("call filterSchemas...");
        String user = identity.getUser();
        Set<String> filterSchemas = new HashSet<>();
        if (this.ADMIN.contains(user)) {
            filterSchemas.addAll(schemaNames);
        } else {
            SecurityUtil instance = SecurityUtil.getInstance();
            Map<String, Set<String>> accessInfo = instance.accessSchemaInfo;
            if (accessInfo.containsKey(user)) {
                Set<String> schemaSet = accessInfo.get(user);
                if (schemaSet != null) {//如果能在配置文件中找到schema，那么就返回找到的
                    filterSchemas.addAll(schemaSet);
                } else {//如果没有在配置文件中找到，那么就返回"dw_t" + user 的schema(前提是user必须和tenant_id一致)
                    if (user.matches("[0-9]+")) {//如果user是纯数字，那么认为user就是tenant_id
                        filterSchemas.add("dw_t".concat(user));
                    }
                }
            }
        }
        return filterSchemas;
    }

    /**
     * 只有root 才能 create table
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanCreateTable(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanCreateTable...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyCreateTable(table.toString());
    }

    /**
     * 只有root 才能 drop table
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanDropTable(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanDropTable...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyDropTable(table.toString());
    }

    /**
     * 只有root 才能 rename table
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanRenameTable(Identity identity, AccessControlContext context, CatalogSchemaTableName table, CatalogSchemaTableName newTable) {
        logger.warn("call checkCanRenameTable...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyRenameTable(table.toString(), newTable.toString());
    }

    /**
     * show table meta when executing SHOW TABLES, SHOW GRANTS etc. in a catalog.
     * 因为有多种不同的途径可以list table，例如 SHOW TABLES, SHOW GRANTS，而且这里只是展示一个错误信息
     * 所以在 filterTables 中也要做限制
     *
     * @param identity
     * @param context
     * @param schema
     */
    @Override
    public void checkCanShowTablesMetadata(Identity identity, AccessControlContext context, CatalogSchemaName schema) {
        logger.warn("call checkCanShowTablesMetadata...");
        String user = identity.getUser().toLowerCase();
        SecurityUtil instance = SecurityUtil.getInstance();
        Map<String, String> userInfo = instance.userInfo;
        if (!this.ADMIN.contains(user)) {
            if (!userInfo.containsKey(user))//非配置用户没有权限
                AccessDeniedException.denyShowTablesMetadata(schema.toString(), "userInfo=" + userInfo);
            else {//如果是配置用户
                if ("hive".equalsIgnoreCase(schema.getCatalogName())) {//如果catalog=hive
                    String schemaName = schema.getSchemaName();
                    Map<String, Set<String>> accessInfo = instance.accessSchemaInfo;//如果配置了schema
                    Set<String> schemaNameSet = accessInfo.get(user);
                    if (schemaNameSet == null || !schemaNameSet.contains(schemaName)) {//如果配置的schema为空，或者配置的schema不包含该schema
                        if (!user.matches("[0-9]+") && !"dw_t".concat(user).equalsIgnoreCase(schemaName)) {//那么校验一下user是否纯数字，并且是否和tenant_id一致
                            AccessDeniedException.denyShowTablesMetadata(schema.toString());
                        }
                    }
                } else {
                    AccessDeniedException.denyShowTablesMetadata(schema.toString());
                }
            }
        }
    }

    /**
     * 控制table的权限
     *
     * @param identity
     * @param context
     * @param catalogName
     * @param tableNames
     * @return
     */
    @Override
    public Set<SchemaTableName> filterTables(Identity identity, AccessControlContext context, String catalogName, Set<SchemaTableName> tableNames) {
        logger.warn("call filterTables...");
        Set<SchemaTableName> tableNameSet = new HashSet<>();
        String user = identity.getUser().toLowerCase();
        if (this.ADMIN.contains(user)) {
            logger.warn("root add " + tableNames.toString());
            tableNameSet.addAll(tableNames);
        } else {
            SecurityUtil instance = SecurityUtil.getInstance();
            Map<String, Set<String>> accessSchemaInfo = instance.accessSchemaInfo;
            Map<String, Set<String>> accessTableInfo = instance.accessTableInfo;
            Set<String> accessSchemaSet = accessSchemaInfo.get(user);
            Set<String> accessTableSet = accessTableInfo.get(user);
            logger.warn("accessSchemaSet = " + accessSchemaSet.toString());
            logger.warn("accessTableSet = " + accessTableSet.toString());
            logger.warn("tableNames = " + tableNames.toString());
            logger.warn("user = " + user.matches("[0-9]+"));
            if (accessSchemaSet != null) {
                for (SchemaTableName tableName : tableNames) {
                    if (accessSchemaSet.contains(tableName.getSchemaName())) {//首先判断schema权限
                        //然后判断有哪些表权限，*代表有所有表的权限
                        if (accessTableSet.contains("*") || accessTableSet.contains(tableName.getTableName())) {
                            logger.debug("accessTableSet = " + accessTableSet + ",current table = " + tableName.getTableName());
                            tableNameSet.add(tableName);
                        }
                    }
                }
            }
            if (user.matches("[0-9]+")) {
                logger.warn("user = " + user.matches("[0-9]+"));
                for (SchemaTableName tableName : tableNames) {
                    logger.warn("current schema = " + tableName.getSchemaName() + ",current table = " + tableName.getTableName());
                    if ("dw_t".concat(user).equalsIgnoreCase(tableName.getSchemaName())) {//首先判断schema权限
                        logger.warn("schema equals");
                        //然后判断有哪些表权限，*代表有所有表的权限
                        if (accessTableSet.contains("*") || accessTableSet.contains(tableName.getTableName())) {
                            logger.warn("add table = " + tableName );
                            tableNameSet.add(tableName);
                        }
                    }
                }
            }
        }
        logger.warn("user: " + user + ",tableSet: " + tableNameSet.toString());
        return tableNameSet;
    }

    /**
     * 只有root可以 add column
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanAddColumn(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanAddColumn...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyAddColumn(table.toString());
    }

    /**
     * 只有root可以 drop column
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanDropColumn(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanDropColumn...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyDropColumn(table.toString());
    }

    /**
     * 只有root可以rename column
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanRenameColumn(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanRenameColumn...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyRenameColumn(table.toString());
    }

    /**
     * @param identity
     * @param context
     * @param table
     * @param columns
     */
    @Override
    public void checkCanSelectFromColumns(Identity identity, AccessControlContext context, CatalogSchemaTableName table, Set<String> columns) {
        logger.warn("call checkCanSelectFromColumns...");
        String user = identity.getUser().toLowerCase();
        SchemaTableName schemaTableName = table.getSchemaTableName();
        String schemaName = schemaTableName.getSchemaName();
        SecurityUtil instance = SecurityUtil.getInstance();
        Map<String, String> userInfo = instance.userInfo;
        String tableName = schemaTableName.getTableName();
        if (!this.ADMIN.contains(user)) {
            if (!userInfo.containsKey(user)) {
                AccessDeniedException.denySelectTable(table.toString());
            } else {
                Map<String, Set<String>> accessInfo = instance.accessSchemaInfo;
                Set<String> schemaSet = accessInfo.get(user);
                Map<String, Set<String>> accessTableInfo = instance.accessTableInfo;
                Set<String> tableSet = accessTableInfo.get(user);
                boolean canAccess = true;
                if (schemaSet == null || !schemaSet.contains(schemaName)) {//先判断schema
                    if (!user.matches("[0-9]+") || !"dw_t".concat(user).equalsIgnoreCase(schemaName)) {
                        canAccess = false;
                    }
                }
                if (!tableSet.contains("*") && !tableSet.contains(tableName)) { //再判断table
                    canAccess = false;
                }
                if (!canAccess) {
                    AccessDeniedException.denySelectTable(table.toString());
                }
            }
        }
    }

    /**
     * 非root不能 insert into table
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanInsertIntoTable(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanInsertIntoTable...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyInsertTable(table.toString());
    }

    /**
     * 非root不能delete from table
     *
     * @param identity
     * @param context
     * @param table
     */
    @Override
    public void checkCanDeleteFromTable(Identity identity, AccessControlContext context, CatalogSchemaTableName table) {
        logger.warn("call checkCanDeleteFromTable...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyDropTable(table.toString());
    }

    /**
     * 非root不能create view
     *
     * @param identity
     * @param context
     * @param view
     */
    @Override
    public void checkCanCreateView(Identity identity, AccessControlContext context, CatalogSchemaTableName view) {
        logger.warn("call checkCanCreateView...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyCreateView(view.toString());
    }

    /**
     * 非root不能drop view
     *
     * @param identity
     * @param context
     * @param view
     */
    @Override
    public void checkCanDropView(Identity identity, AccessControlContext context, CatalogSchemaTableName view) {
        logger.warn("call checkCanDropView...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyDropView(view.toString());
    }

    /**
     * 非root不能 Create View With Select From Columns
     *
     * @param identity
     * @param context
     * @param table
     * @param columns
     */
    @Override
    public void checkCanCreateViewWithSelectFromColumns(Identity identity, AccessControlContext context, CatalogSchemaTableName table, Set<String> columns) {
        logger.warn("call checkCanCreateViewWithSelectFromColumns...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyCreateViewWithSelect(table.getSchemaTableName().getTableName(), identity);
    }

    /**
     * 非root不能 set catalog session property
     *
     * @param identity
     * @param context
     * @param catalogName
     * @param propertyName
     */
    @Override
    public void checkCanSetCatalogSessionProperty(Identity identity, AccessControlContext context, String catalogName, String propertyName) {
        logger.warn("call checkCanSetCatalogSessionProperty...");
        SecurityUtil instance = SecurityUtil.getInstance();
        Map<String, String> userInfo = instance.userInfo;
        if (!this.ADMIN.contains(userInfo)) {
            if (!userInfo.containsKey(identity.getUser().toLowerCase()))
                AccessDeniedException.denySetCatalogSessionProperty(catalogName, propertyName);
        }
    }

    /**
     * 非root不能grant table privilege
     *
     * @param identity
     * @param context
     * @param privilege
     * @param table
     * @param grantee
     * @param withGrantOption
     */
    @Override
    public void checkCanGrantTablePrivilege(Identity identity, AccessControlContext context, Privilege privilege, CatalogSchemaTableName table, PrestoPrincipal grantee, boolean withGrantOption) {
        logger.warn("call checkCanGrantTablePrivilege...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyGrantTablePrivilege(table.toString(), identity.getUser());
    }

    /**
     * 非root不能 revoke table privilege
     *
     * @param identity
     * @param context
     * @param privilege
     * @param table
     * @param revokee
     * @param grantOptionFor
     */
    @Override
    public void checkCanRevokeTablePrivilege(Identity identity, AccessControlContext context, Privilege privilege, CatalogSchemaTableName table, PrestoPrincipal revokee, boolean grantOptionFor) {
        logger.warn("call checkCanRevokeTablePrivilege...");
        if (!this.ADMIN.contains(identity.getUser().toLowerCase()))
            AccessDeniedException.denyRevokeTablePrivilege(table.toString(), identity.getUser());
    }
}
