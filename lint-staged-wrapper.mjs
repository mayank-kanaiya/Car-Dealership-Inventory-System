import { execSync } from 'child_process';
import { resolve, relative } from 'path';

const cwd = resolve('vehicle-inventory-ui');
const tool = process.argv[2];
const files = process.argv.slice(3);

const relativeFiles = files.map((f) => relative(cwd, resolve(f)).replace(/\\/g, '/'));
const fixFlag = tool === 'eslint' ? '--fix ' : '';
const cmd = `npx ${tool} ${fixFlag}${relativeFiles.join(' ')}`;

execSync(cmd, { cwd, stdio: 'inherit' });
