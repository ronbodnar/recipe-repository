export interface Recipe {
  id: string;
  title: string;
  description: string;
  source: string;
  visibility: RecipeVisibility;
  images: string[];
  authorSubject: string;
  createdAt: Date;
  updatedAt: Date;
  courses: Course[];
  cuisines: Cuisine[];
  mealTypes: MealType[];
  dietTypes: DietType[];
  variants: RecipeVariant[];
}

export interface RecipeSummary {
  id: string;
  title: string;
  description: string;
  imageIds: string[];
}

export interface RecipeVariant {
  id?: string;
  name: string | null;
  cookingMethod: CookingMethod;
  prepTime: number;
  cookTime: number;
  yield: string | null;
  ingredients: string[];
  instructions: string[];
  notes: string[];
}

export const COOKING_METHODS = [
  'AIR_FRYER',
  'BARBECUE',
  'GRILL',
  'MICROWAVE',
  'NO_COOK',
  'OVEN',
  'PRESSURE_COOKER',
  'SLOW_COOKER',
  'SMOKER',
  'STOVETOP',
] as const;

export const COURSE_TYPES = [
  'APPETIZER',
  'MAIN_COURSE',
  'SIDE_DISH',
  'DESSERT',
  'BEVERAGE',
  'SOUP',
  'SALAD',
] as const;

export const CUISINE_TYPES = [
  'AMERICAN',
  'ITALIAN',
  'MEXICAN',
  'INDIAN',
  'CHINESE',
  'JAPANESE',
  'FRENCH',
  'GERMAN',
  'SPANISH',
  'GREEK',
  'KOREAN',
  'SALVADORAN',
  'MEDITERRANEAN',
] as const;

export const DIET_TYPES = [
  'GLUTEN_FREE',
  'VEGETARIAN',
  'DAIRY_FREE',
  'KETO',
  'VEGAN',
  'LOW_CARB',
] as const;

export const MEAL_TYPES = ['BREAKFAST', 'BRUNCH', 'LUNCH', 'DINNER', 'SNACK'] as const;

export const VISIBILITY_TYPES = ['PUBLIC', 'PRIVATE', 'GROUP'] as const;

export type CookingMethod = (typeof COOKING_METHODS)[number];
export type Course = (typeof COURSE_TYPES)[number];
export type Cuisine = (typeof CUISINE_TYPES)[number];
export type DietType = (typeof DIET_TYPES)[number];
export type MealType = (typeof MEAL_TYPES)[number];
export type RecipeVisibility = (typeof VISIBILITY_TYPES)[number];
