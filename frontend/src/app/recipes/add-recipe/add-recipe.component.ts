import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-recipe',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './add-recipe.component.html',
  styleUrl: './add-recipe.component.css'
})
export class AddRecipeComponent {
  addRecipeForm = this.formBuilder.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    prepTime: ['', Validators.required],
    cookTime: ['', Validators.required],
    ingredients: ['', Validators.required],
    instructions: ['', Validators.required],
  });

  constructor(private formBuilder: FormBuilder) {}

  onSubmit() : void {
    console.log("submitting")
  }
}
