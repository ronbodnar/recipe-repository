import { Ingredient } from "./ingredient";
import { Instruction } from "./instruction";
import { RecipeImage } from "./recipe-image";
import { User } from "./user";

export class Recipe {

    id!: number;

    title!: string;

    description!: string;

    prepTime!: number;

    cookTime!: number;

    servings!: number;

    author!: User;

    images!: Array<RecipeImage>;

    ingredients!: Array<Ingredient>;

    instructions!: Array<Instruction>;
}
