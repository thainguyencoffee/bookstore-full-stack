export interface Book {
  isbn: string,
  title: string,
  author: string,
  publisher: string,
  supplier: string,
  description?: string,
  price: number,
  purchases: number,
  inventory: number,
  language: string,
  coverType: string,
  numberOfPages: number,
  measure: {
    width: number,
    height: number,
    thickness: number,
    weight: number,
  }
  photos?: string[],
  createdAt?: string,
  createdBy?: string,
  lastModifiedAt?: string,
  lastModifiedBy?: string,
}
