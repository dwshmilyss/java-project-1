package com.lf.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.MetaStoreEventListener;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.events.*;
import org.apache.hadoop.hive.metastore.tools.SQLGenerator;

import java.sql.Connection;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/1/10
 **/
public class HiveEventTest extends MetaStoreEventListener {
    public HiveEventTest(Configuration config) {
        super(config);
    }

    @Override
    public void onConfigChange(ConfigChangeEvent tableEvent) throws MetaException {
        super.onConfigChange(tableEvent);
    }

    @Override
    public void onCreateTable(CreateTableEvent tableEvent) throws MetaException {
        super.onCreateTable(tableEvent);
    }

    @Override
    public void onDropTable(DropTableEvent tableEvent) throws MetaException {
        super.onDropTable(tableEvent);
    }

    @Override
    public void onAlterTable(AlterTableEvent tableEvent) throws MetaException {
        super.onAlterTable(tableEvent);
    }

    @Override
    public void onAddPartition(AddPartitionEvent partitionEvent) throws MetaException {
        super.onAddPartition(partitionEvent);
    }

    @Override
    public void onDropPartition(DropPartitionEvent partitionEvent) throws MetaException {
        super.onDropPartition(partitionEvent);
    }

    @Override
    public void onAlterPartition(AlterPartitionEvent partitionEvent) throws MetaException {
        super.onAlterPartition(partitionEvent);
    }

    @Override
    public void onCreateDatabase(CreateDatabaseEvent dbEvent) throws MetaException {
        super.onCreateDatabase(dbEvent);
    }

    @Override
    public void onDropDatabase(DropDatabaseEvent dbEvent) throws MetaException {
        super.onDropDatabase(dbEvent);
    }

    @Override
    public void onAlterDatabase(AlterDatabaseEvent dbEvent) throws MetaException {
        super.onAlterDatabase(dbEvent);
    }

    @Override
    public void onLoadPartitionDone(LoadPartitionDoneEvent partSetDoneEvent) throws MetaException {
        super.onLoadPartitionDone(partSetDoneEvent);
    }

    @Override
    public void onCreateFunction(CreateFunctionEvent fnEvent) throws MetaException {
        super.onCreateFunction(fnEvent);
    }

    @Override
    public void onDropFunction(DropFunctionEvent fnEvent) throws MetaException {
        super.onDropFunction(fnEvent);
    }

    @Override
    public void onInsert(InsertEvent insertEvent) throws MetaException {
        super.onInsert(insertEvent);
    }

    @Override
    public void onAddPrimaryKey(AddPrimaryKeyEvent addPrimaryKeyEvent) throws MetaException {
        super.onAddPrimaryKey(addPrimaryKeyEvent);
    }

    @Override
    public void onAddForeignKey(AddForeignKeyEvent addForeignKeyEvent) throws MetaException {
        super.onAddForeignKey(addForeignKeyEvent);
    }

    @Override
    public void onAddUniqueConstraint(AddUniqueConstraintEvent addUniqueConstraintEvent) throws MetaException {
        super.onAddUniqueConstraint(addUniqueConstraintEvent);
    }

    @Override
    public void onAddNotNullConstraint(AddNotNullConstraintEvent addNotNullConstraintEvent) throws MetaException {
        super.onAddNotNullConstraint(addNotNullConstraintEvent);
    }

    @Override
    public void onDropConstraint(DropConstraintEvent dropConstraintEvent) throws MetaException {
        super.onDropConstraint(dropConstraintEvent);
    }

    @Override
    public void onCreateISchema(CreateISchemaEvent createISchemaEvent) throws MetaException {
        super.onCreateISchema(createISchemaEvent);
    }

    @Override
    public void onAlterISchema(AlterISchemaEvent alterISchemaEvent) throws MetaException {
        super.onAlterISchema(alterISchemaEvent);
    }

    @Override
    public void onDropISchema(DropISchemaEvent dropISchemaEvent) throws MetaException {
        super.onDropISchema(dropISchemaEvent);
    }

    @Override
    public void onAddSchemaVersion(AddSchemaVersionEvent addSchemaVersionEvent) throws MetaException {
        super.onAddSchemaVersion(addSchemaVersionEvent);
    }

    @Override
    public void onAlterSchemaVersion(AlterSchemaVersionEvent alterSchemaVersionEvent) throws MetaException {
        super.onAlterSchemaVersion(alterSchemaVersionEvent);
    }

    @Override
    public void onDropSchemaVersion(DropSchemaVersionEvent dropSchemaVersionEvent) throws MetaException {
        super.onDropSchemaVersion(dropSchemaVersionEvent);
    }

    @Override
    public void onCreateCatalog(CreateCatalogEvent createCatalogEvent) throws MetaException {
        super.onCreateCatalog(createCatalogEvent);
    }

    @Override
    public void onAlterCatalog(AlterCatalogEvent alterCatalogEvent) throws MetaException {
        super.onAlterCatalog(alterCatalogEvent);
    }

    @Override
    public void onDropCatalog(DropCatalogEvent dropCatalogEvent) throws MetaException {
        super.onDropCatalog(dropCatalogEvent);
    }

    @Override
    public void onOpenTxn(OpenTxnEvent openTxnEvent, Connection dbConn, SQLGenerator sqlGenerator) throws MetaException {
        super.onOpenTxn(openTxnEvent, dbConn, sqlGenerator);
    }

    @Override
    public void onCommitTxn(CommitTxnEvent commitTxnEvent, Connection dbConn, SQLGenerator sqlGenerator) throws MetaException {
        super.onCommitTxn(commitTxnEvent, dbConn, sqlGenerator);
    }

    @Override
    public void onAbortTxn(AbortTxnEvent abortTxnEvent, Connection dbConn, SQLGenerator sqlGenerator) throws MetaException {
        super.onAbortTxn(abortTxnEvent, dbConn, sqlGenerator);
    }

    @Override
    public void onAllocWriteId(AllocWriteIdEvent allocWriteIdEvent, Connection dbConn, SQLGenerator sqlGenerator) throws MetaException {
        super.onAllocWriteId(allocWriteIdEvent, dbConn, sqlGenerator);
    }

    @Override
    public Configuration getConf() {
        return super.getConf();
    }

    @Override
    public void setConf(Configuration config) {
        super.setConf(config);
    }
}
