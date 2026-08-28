export function asCamelCase(str: string): string {
  if (!str) {
    return str;
  }
  str = str.toLowerCase();
  for (let i = 0; i < str.length; i++) {
    if (str[i] === '_') {
      const nextChar = str[i + 1];
      if (nextChar) {
        str = str.slice(0, i) + nextChar.toUpperCase() + str.slice(i + 2);
      }
      i++;
    }
  }
  return str;
}
