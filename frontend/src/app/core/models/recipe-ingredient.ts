import { Ingredient } from "./ingredient";
import { MeasurementType } from "../enums/measurement-type";
import { Recipe } from "./recipe";

export class RecipeIngredient {

    id!: number;

    recipe!: Recipe;

    ingredient!: Ingredient;

    quantity!: number;

    measurementType!: MeasurementType;
}
