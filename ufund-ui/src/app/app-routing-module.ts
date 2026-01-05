import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CupboardList } from './cupboard-list/cupboard-list';
import { Login } from './login/login';
import { CreateAccount } from './create-account/create-account';
import { ForgotPassword } from './forgot-password/forgot-password';

export const routes: Routes = [
  { path: 'home', component: CupboardList, title: 'Home' },
  { path: '', component: Login, title: 'Login' },
  { path: 'create', component: CreateAccount, title: 'Create Account' },
  { path: 'forgot', component: ForgotPassword, title: 'Forgot Password' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
