
export interface CustomError {
  entity: string,
  property: string,
  invalidValue: any,
  message: string,
}

export interface ApiError {
  errors: CustomError[]
}
