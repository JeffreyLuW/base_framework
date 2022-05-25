package com.meide.dbengine.utils;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于动态调用mapper。
 */
public class MapperHelper {

    Configuration configuration;
    SqlSession sqlSession;
    boolean lowerKey = false;

    public MapperHelper(Configuration configuration, SqlSession sqlSession) {
        this.configuration = configuration;
        this.sqlSession = sqlSession;
    }

    public void setLowerKey(boolean lowerKey) {
        this.lowerKey = lowerKey;
    }

    /**
     * mybatis执行sql语句
     * 可以用来调用指定的sql语句。
     *
     * @param statement     xml中的 namesapce.id
     * @param returnType    返回类型 HashMap|List
     * @param mapperType    泛型时的第二个参数类型
     * @param needWrapParam 默认true 自动将参数映射到 params 对象。 false则不处理。
     * @return
     */
    public Object exeStatementWithType(String statement, Object param, Class returnType, Class mapperType, boolean needWrapParam) {
        if (null == param) {
            param = new HashMap<>();
        }
        if (needWrapParam) {
            HashMap paramMap = new HashMap();
            paramMap.put("params", param);
            param = paramMap;
        }

        Object result = null;
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        switch (sqlCommandType) {
            case INSERT: {
                result = rowCountResult(statement, returnType, sqlSession.insert(statement, param));
                break;
            }
            case UPDATE: {
                result = rowCountResult(statement, returnType, sqlSession.update(statement, param));
                break;
            }
            case DELETE: {
                result = rowCountResult(statement, returnType, sqlSession.delete(statement, param));
                break;
            }
            case SELECT:
                if (null == returnType || void.class.equals(returnType)) {
                    executeWithResultHandler(statement, configuration, sqlSession, param);
                    result = null;
                } else if (List.class.isAssignableFrom(returnType)) {
                    result = executeForMany(statement, returnType, mapperType, sqlSession, param);
                    if (lowerKey && Map.class.isAssignableFrom(mapperType)) {
                        result = EngineUtil.mapKeyToLower(result);
                    }
                }
//                else if (Map.class.isAssignableFrom(returnType)) {
//                    result = executeForMap(statement, sqlSession, null);
//                }
                else {
                    result = sqlSession.selectOne(statement, param);
                    if (lowerKey && result instanceof Map) {
                        result = EngineUtil.mapKeyToLower(result);
                    }
                }
                //格式化一下结果。主要是日期。
                result = EngineUtil.formatMapperResult(result);
                break;
            case FLUSH:
                result = sqlSession.flushStatements();
                break;
            default:
                throw new BindingException("Unknown execution method for: " + statement);
        }
        return result;
    }

    private void executeWithResultHandler(String statement, Configuration configuration, SqlSession sqlSession, Object param) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        if (!StatementType.CALLABLE.equals(ms.getStatementType())
                && void.class.equals(ms.getResultMaps().get(0).getType())) {
            throw new BindingException("method " + statement
                    + " needs either a @ResultMap annotation, a @ResultType annotation,"
                    + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }
//        Object param = null;//method.convertArgsToSqlCommandParam(args);
        sqlSession.select(statement, param, null);
    }

    private <E> Object executeForMany(String statement, Class returnType, Class mapperType, SqlSession sqlSession, Object param) {
        List<E> result;
//        Object param = null;// method.convertArgsToSqlCommandParam(args);
        result = sqlSession.selectList(statement, param);
        // issue #510 Collections & arrays support
        if (!returnType.isAssignableFrom(result.getClass())) {
            if (returnType.isArray()) {
                return convertToArray(result, mapperType);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), returnType, result);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list, Class mapperType) {
        Class<?> arrayComponentType = mapperType;
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        } else {
            return list.toArray((E[]) array);
        }
    }

    private <E> Object convertToDeclaredCollection(Configuration config, Class returnType, List<E> list) {
        Object collection = config.getObjectFactory().create(returnType);
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }


    private <K, V> Map<K, V> executeForMap(String statement, SqlSession sqlSession, Object[] args) {
        Map<K, V> result;
        Object param = null;// method.convertArgsToSqlCommandParam(args);
        result = sqlSession.selectMap(statement, param, null);
        return result;
    }


    private Object rowCountResult(String statement, Class returnType, int rowCount) {
        Object result = null;
        if (void.class.equals(returnType)) {
            result = null;
        } else if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
            result = rowCount;
        } else if (Long.class.equals(returnType) || Long.TYPE.equals(returnType)) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
            result = rowCount > 0;
        } else {
            //默认就是整数型 受影响行数
            result = rowCount;
        }
        return result;
    }

}
