export interface Page<T> {
  content: T[],
  pageable: {
    pageNumber: number,
    pageSize: number
  },
  totalElements: number
}
