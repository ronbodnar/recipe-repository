export interface Recipe {
  id: string;
  title: string;
  description: string;
  images: string[];
  authorId: string;
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
  servings: number;
  ingredients: string[];
  instructions: string[];
  notes: string[];
}

export const COOKING_METHODS = [
  'Air Fryer',
  'Barbecue',
  'Grill',
  'Microwave',
  'No-Cook',
  'Oven',
  'Pressure Cooker',
  'Slow Cooker',
  'Smoker',
  'Stovetop',
] as const;

export const COURSE_TYPES = [
  'Appetizer',
  'Main Course',
  'Side Dish',
  'Dessert',
  'Beverage',
  'Soup',
  'Salad',
] as const;

export const CUISINE_TYPES = [
  'American',
  'Italian',
  'Mexican',
  'Indian',
  'Chinese',
  'Japanese',
  'French',
  'German',
  'Spanish',
  'Greek',
  'Korean',
  'Salvadoran',
  'Mediterranean',
] as const;

export const DIET_TYPES = [
  'Gluten-Free',
  'Vegetarian',
  'Dairy-Free',
  'Keto',
  'Vegan',
  'Low Carb',
] as const;

export const MEAL_TYPES = ['Breakfast', 'Brunch', 'Lunch', 'Dinner', 'Snack'] as const;

export type CookingMethod = (typeof COOKING_METHODS)[number];
export type Course = (typeof COURSE_TYPES)[number];
export type Cuisine = (typeof CUISINE_TYPES)[number];
export type DietType = (typeof DIET_TYPES)[number];
export type MealType = (typeof MEAL_TYPES)[number];
