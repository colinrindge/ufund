import { BasketNeed } from './basketNeed';

export interface User {
  id: number;
  userName: string;
  password: string;
  security: Array<string>;
  basket: Array<BasketNeed>;
  restricted: boolean;
}
