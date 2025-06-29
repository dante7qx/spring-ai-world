export interface WeatherResponse<T> {
  code: string
  data: T[]
  refer: APIRefer
}

interface APIRefer {
  sources: string[]
  license: string[]
}

export interface CityLocation {
  id: string
  name: string
  lat: string
  lon: string
  adm2: string
  adm1: string
  country: string
  tz: string
  utcOffset: string
  isDst: string
  type: string
  rank: string
  fxLink: string
}