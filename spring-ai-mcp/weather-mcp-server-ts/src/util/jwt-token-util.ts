import * as fs from 'fs'
import * as path from 'path'
import * as crypto from 'crypto'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const privateKeyPath = process.env.PRIVATE_KEY_PATH || '../../keys/qweather-ed25519-private.pem'
const fullPath = path.resolve(__dirname, privateKeyPath)

interface JwtHeader {
  alg: string
  kid: string
}

interface JwtPayload {
  sub: string
  iat: number
  exp: number
}

export class JwtEd25519Util {
  /**
   * 生成 JWT，默认私钥路径为 'qweather-ed25519-private.pem'
   * @param kid key id
   * @param sub subject
   */
  static async generateJwt(kid: string, sub: string): Promise<string> {
    return this.generateJwtWithKeyPath(fullPath, kid, sub);
  }

  /**
   * 生成 JWT
   * @param privateKeyPath Ed25519 私钥文件路径，PEM 格式
   * @param kid key id
   * @param sub subject
   */
  static async generateJwtWithKeyPath(privateKeyPath: string, kid: string, sub: string): Promise<string> {
    const alg = 'EdDSA';

    // iat = 当前时间戳 - 30秒
    const iat = Math.floor(Date.now() / 1000) - 30
    // exp = 当前时间戳 + 20小时
    const exp = Math.floor(Date.now() / 1000) + 20 * 3600

    // 1. 构建 header 和 payload
    const header: JwtHeader = {
      alg,
      kid,
    }

    const payload: JwtPayload = {
      sub,
      iat,
      exp,
    }

    // 2. JSON 转字符串
    const headerJson = JSON.stringify(header)
    const payloadJson = JSON.stringify(payload)

    // 3. Base64URL 编码
    const encodedHeader = this.base64UrlEncode(Buffer.from(headerJson, 'utf8'))
    const encodedPayload = this.base64UrlEncode(Buffer.from(payloadJson, 'utf8'))

    // 4. 拼接签名输入
    const signingInput = `${encodedHeader}.${encodedPayload}`;

    // 5. 读取私钥并签名
    const privateKey = await this.loadEd25519PrivateKey(privateKeyPath);
    const signature = this.signEd25519(privateKey, Buffer.from(signingInput, 'utf8'));

    // 6. Base64URL 编码签名
    const encodedSignature = this.base64UrlEncode(signature);

    // 7. 拼接完整 JWT
    return `${signingInput}.${encodedSignature}`;
  }

  private static base64UrlEncode(buffer: Buffer): string {
    return buffer
      .toString('base64')
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '')
  }

  private static async loadEd25519PrivateKey(privateKeyPath: string): Promise<crypto.KeyObject> {
    // 读取 PEM 文件内容
    const fullPath = path.resolve(__dirname, privateKeyPath)
    if (!fs.existsSync(fullPath)) {
      throw new Error(`Private key file not found: ${fullPath}`)
    }
    const pem = await fs.promises.readFile(fullPath, { encoding: 'utf8' })

    // Node.js crypto 支持直接从 PEM 加载 Ed25519 私钥
    // 这里假设 PEM 是 PKCS#8 格式的私钥
    return crypto.createPrivateKey({
      key: pem,
      format: 'pem',
      type: 'pkcs8',
    })
  }

  private static signEd25519(privateKey: crypto.KeyObject, data: Buffer): Buffer {
    // 使用 crypto.sign 进行 Ed25519 签名
    // Node.js 15+ 支持 Ed25519 签名算法
    return crypto.sign(null, data, privateKey);
  }
}
