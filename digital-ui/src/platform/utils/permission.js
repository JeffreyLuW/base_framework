import store from '@/platform/store'

/***
 * 检测权限数组，如[{permi:['system:user:add'],role:['admin'],..elseOptions}]
 * @return 返回权限匹配的行.
 */
export function checkArray(arr) {
  return arr.filter((item) => {
    let hasPermi = item.permi && item.permi instanceof Array && item.permi.length > 0;
    let hasRole = item.role && item.role instanceof Array && item.role.length > 0;

    if (!hasPermi && !hasRole) {
      return true;
    }
    if (hasPermi && !hasRole) {
      return checkPermi(item.permi);
    }
    if (!hasPermi && hasRole) {
      return checkRole(item.role);
    }
    return checkPermi(item.permi) && checkRole(item.role);
  });
}

/**
 * 检测权限数组，如[{type:'',permi:['system:user:add'],role:['admin'],..elseOptions}]
 * @return 返回权限匹配的权限描述
 */
export function checkArrayToPermMap(arr = [], keyName = 'type') {
  let opList = checkArray(arr);
  let permDesc = {};
  opList.forEach((row) => {
    permDesc[row[keyName]] = true;
  });
  return permDesc;
}

/**
 * 字符权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export function checkPermi(value) {

  if (value && value instanceof Array && value.length > 0) {
    const permissions = store.getters && store.getters.permissions;
    
    const permissionDatas = value;
    const all_permission = "*:*:*";
    const hasPermission = permissions.some(permission => {
      return all_permission === permission || permissionDatas.includes(permission)
    });

    if (!hasPermission) {
      return false
    }
    return true
  } else {
    console.error(`need perms! Like checkPermi="['system:user:add','system:user:edit']"`)
    return false
  }
}

/**
 * 角色权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export function checkRole(value) {
  if (value && value instanceof Array && value.length > 0) {
    const roles = store.getters && store.getters.roles
    const permissionRoles = value

    const hasRole = roles.some(role => {
      return permissionRoles.includes(role)
    })

    if (!hasRole) {
      return false
    }
    return true
  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`)
    return false
  }
}
