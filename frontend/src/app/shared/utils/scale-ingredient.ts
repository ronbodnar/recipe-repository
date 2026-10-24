const UNICODE_FRACTIONS: Record<string, number> = {
  '¼': 1 / 4,
  '½': 1 / 2,
  '¾': 3 / 4,
  '⅐': 1 / 7,
  '⅑': 1 / 9,
  '⅒': 1 / 10,
  '⅓': 1 / 3,
  '⅔': 2 / 3,
  '⅕': 1 / 5,
  '⅖': 2 / 5,
  '⅗': 3 / 5,
  '⅘': 4 / 5,
  '⅙': 1 / 6,
  '⅚': 5 / 6,
  '⅛': 1 / 8,
  '⅜': 3 / 8,
  '⅝': 5 / 8,
  '⅞': 7 / 8,
};

const UNICODE_FRACTION_CHARS = Object.keys(UNICODE_FRACTIONS).join('');
// Mixed number, simple fraction, decimal/integer with optional unicode fraction, or lone unicode fraction.
const QUANTITY = `\\d+\\s+\\d+\\/\\d+|\\d+\\/\\d+|\\d+(?:\\.\\d+)?\\s?[${UNICODE_FRACTION_CHARS}]|[${UNICODE_FRACTION_CHARS}]|\\d+(?:\\.\\d+)?`;
const LEADING_RANGE = new RegExp(
  `^(\\s*)(${QUANTITY})(?:(\\s*(?:-|–|to)\\s*)(${QUANTITY}))?(?![\\d/])`,
  'i',
);
const PARENTHETICAL_AMOUNT = new RegExp(`\\((\\s*)(${QUANTITY})(?=\\s*[a-zA-Z]+\\s*\\))`, 'g');

const FRACTION_STEPS: [number, string][] = [
  [1 / 8, '1/8'],
  [1 / 4, '1/4'],
  [1 / 3, '1/3'],
  [3 / 8, '3/8'],
  [1 / 2, '1/2'],
  [5 / 8, '5/8'],
  [2 / 3, '2/3'],
  [3 / 4, '3/4'],
  [7 / 8, '7/8'],
];

function parseQuantity(text: string): number {
  const value = text.trim();

  const mixed = /^(\d+)\s+(\d+)\/(\d+)$/.exec(value);
  if (mixed) {
    return Number(mixed[1]) + Number(mixed[2]) / Number(mixed[3]);
  }

  const fraction = /^(\d+)\/(\d+)$/.exec(value);
  if (fraction) {
    return Number(fraction[1]) / Number(fraction[2]);
  }

  const lastChar = value.slice(-1);
  if (lastChar in UNICODE_FRACTIONS) {
    const whole = value.slice(0, -1).trim();
    return (whole ? Number(whole) : 0) + UNICODE_FRACTIONS[lastChar];
  }

  return Number(value);
}

function formatQuantity(value: number): string {
  const whole = Math.floor(value);
  const remainder = value - whole;

  if (remainder < 0.04) {
    return `${whole}`;
  }
  if (remainder > 0.96) {
    return `${whole + 1}`;
  }

  const match = FRACTION_STEPS.find(([step]) => Math.abs(step - remainder) < 0.02);
  if (match) {
    return whole > 0 ? `${whole} ${match[1]}` : match[1];
  }

  return `${Math.round(value * 100) / 100}`;
}

function scaleQuantity(text: string, scale: number): string {
  return formatQuantity(parseQuantity(text) * scale);
}

/** Scales the leading quantity (or range) and parenthetical amounts such as "(300g)". */
export function scaleIngredient(ingredient: string, scale: number): string {
  if (scale === 1) {
    return ingredient;
  }

  const scaled = ingredient.replace(
    LEADING_RANGE,
    (_match, space: string, first: string, separator?: string, second?: string) =>
      `${space}${scaleQuantity(first, scale)}${
        separator && second ? `${separator}${scaleQuantity(second, scale)}` : ''
      }`,
  );

  return scaled.replace(
    PARENTHETICAL_AMOUNT,
    (_match, space: string, amount: string) => `(${space}${scaleQuantity(amount, scale)}`,
  );
}
