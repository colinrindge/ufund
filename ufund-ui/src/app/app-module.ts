import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Header } from './header/header';
import { Overlay } from './admin-overlay-component/admin-overlay-component';
import { EditModalComponent } from './edit-modal-component/edit-modal-component';
import { ReactiveFormsModule } from '@angular/forms';
import { CupboardList } from './cupboard-list/cupboard-list';
import { AddToBasket } from './add-to-basket/add-to-basket';
import { CreateModalComponent } from './create-modal-component/create-modal-component';
import { CreateNeed } from './create-need/create-need';
import { BasketOverlay } from './basket-component/basket-component';
import { Login } from './login/login';
import { provideRouter } from '@angular/router';
import { routes } from './app-routing-module';
import { CreateAccount } from './create-account/create-account';
import { ChatBox } from './chat-box/chat-box';
import { BeeIcon } from './bee-icon/bee-icon';
import { MarkdownModule } from 'ngx-markdown';
import { ForgotPassword } from './forgot-password/forgot-password';
import { EditUser } from './edit-user/edit-user';
import { EditAdmin } from './edit-admin/edit-admin';

@NgModule({
  declarations: [
    App,
    Header,
    Overlay,
    EditModalComponent,
    CupboardList,
    AddToBasket,
    CreateModalComponent,
    CreateNeed,
    BasketOverlay,
    Login,
    CreateAccount,
    ChatBox,
    BeeIcon,
    ForgotPassword,
    EditUser,
    EditAdmin,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MarkdownModule.forRoot(),
  ],
  providers: [
    provideBrowserGlobalErrorListeners(), 
    provideRouter(routes)
  ],
  bootstrap: [App],
})
export class AppModule {}
