/*
 * Copyright 2010-2018 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.database.hsqldb;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.Connection;
import org.flywaydb.core.internal.database.Schema;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * HSQLDB connection.
 */
public class HSQLDBConnection extends Connection<HSQLDBDatabase> {
    HSQLDBConnection(Configuration configuration, HSQLDBDatabase database, java.sql.Connection connection



    ) {
        super(configuration, database, connection, Types.VARCHAR



        );
    }

    @Override
    protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
        ResultSet resultSet = null;
        String schema = null;

        try {
            resultSet = database.getJdbcMetaData().getSchemas();
            while (resultSet.next()) {
                if (resultSet.getBoolean("IS_DEFAULT")) {
                    schema = resultSet.getString("TABLE_SCHEM");
                    break;
                }
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return schema;
    }

    @Override
    public void doChangeCurrentSchemaOrSearchPathTo(String schema) throws SQLException {
        jdbcTemplate.execute("SET SCHEMA " + database.quote(schema));
    }

    @Override
    public Schema getSchema(String name) {
        return new HSQLDBSchema(jdbcTemplate, database, name);
    }
}