import {McpServer} from "@modelcontextprotocol/sdk/server/mcp.js"
import {StdioServerTransport} from "@modelcontextprotocol/sdk/server/stdio.js"
import {z} from "zod"

import {JwtEd25519Util} from "./util/jwt-token-util.js"
import {CityLocation, WeatherResponse} from "./types/weather.js"

const API_BASE = "https://je4ky33383.re.qweatherapi.com"
let JWT_TOKEN = ""

// Create server instance
const server = new McpServer({
  name: "weather",
  version: "1.0.0",
  capabilities: {
    resources: {},
    tools: {},
  },
})

/**
 * 构造天气请求Request
 *
 * @param url
 */
async function markWeatherRequest<T>(url: string): Promise<T | null> {
  await JwtEd25519Util.generateJwt('K4WMJYTMGU', '242J3HRM2A').then(jwt =>  JWT_TOKEN = jwt)
  const uri = API_BASE + url

  const response = await fetch(uri, {
    method: "GET",
    headers: {
      "accept": "application/json",
      "Content-Type": "application/json",
      "Authorization": `Bearer ${JWT_TOKEN}`,
    },
  })
  try {
    if (!response.ok) {
      return null
    }
    return (await response.json()) as T
  } catch (error) {
    console.error(`Error Make Weather Request: ${error}`)
    return null
  }

}

// Start the server
// server.start();

// Test request
// markWeatherRequest<WeatherResponse<CityLocation>>('/geo/v2/city/lookup?location=beij').then(weatherResponse => {
//  console.log(weatherResponse)
// })



// 注册 weather mcp server tools
server.tool(
    'lookUpCity',
    "获取城市的基本信息，包括城市的Location ID（你需要这个ID去查询天气），多语言名称、经纬度、时区、海拔、Rank值、归属上级行政区域、所在行政区域等",
    {
      location: z.string().describe("The location to look up"),
    },
    async ({location}) => {
      const cityData = await markWeatherRequest<WeatherResponse<CityLocation>>('/geo/v2/city/lookup?location=' + location)
      return {
        content: [{
          type: "text",
          text: JSON.stringify(cityData, null, 2),
        }],
      }
    }
)

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("Weather MCP Server running on stdio");
}

main().catch((error) => {
  console.error("Fatal error in main():", error);
  process.exit(1);
});