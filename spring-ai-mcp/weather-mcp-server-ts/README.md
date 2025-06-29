
## 步骤

### 1. 创建项目
```bash
mkdir weather-mcp-server-ts
cd weather-mcp-server-ts
pnpm init
pnpm install @modelcontextprotocol/sdk zod
pnpm install -D @types/node typescript

mkdir src
```
### 2. 修改 package.json

在 package.json 文件中添加以下内容：
```json
{
  "type": "module",
  "bin": {
    "weather": "./build/index.js"
  },
  "scripts": {
    "build": "tsc && chmod 755 build/index.js"
  },
  "files": ["build"]
}
```

## 3. 创建 tsconfig.json

在根目录下创建 tsconfig.json 文件，内容如下：
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "Node16",
    "moduleResolution": "Node16",
    "outDir": "./build",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules"]
}
```