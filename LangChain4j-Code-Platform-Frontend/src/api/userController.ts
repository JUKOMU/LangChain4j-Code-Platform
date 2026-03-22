// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 PUT /user */
export async function updateUser(body: API.UserUpdateDto, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user */
export async function addUser(body: API.UserAddDto, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/user', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /user */
export async function deleteUser(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/${param0} */
export async function getUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.BaseResponseUserVo>(`/user/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get/login */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVo>('/user/get/login', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/list */
export async function getUsers(body: API.UserQueryDto, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageUserVo>('/user/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/login */
export async function login(body: API.UserLoginDto, options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVo>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/logout', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/register */
export async function register(body: API.UserRegisterDto, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
