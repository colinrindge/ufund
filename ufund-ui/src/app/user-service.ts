import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from './user';
import { BasketNeed } from './basketNeed';
import { Need } from './need';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  currentUser: User = {
    id: 0,
    userName: 'Guest',
    basket: [],
    security: [],
    password: '',
    restricted: false,
  };

  constructor(private http: HttpClient) {}

  private usersUrl = 'http://localhost:8080/users'; // URL to web api
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error); // log to console instead

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** POST: add a new user to the server */
  addUser(user: User): Observable<User> {
    return this.http
      .post<User>(this.usersUrl, user)
      .pipe(catchError(this.handleError<User>('addUser')));
  }

  addNeed(user: User, need: Need): Observable<User> {
    console.log(user.id);
    return this.http
      .put<User>(`${this.usersUrl}/${user.id}/basket`, need, this.httpOptions)
      .pipe(catchError(this.handleError<User>('addNeed')));
  }

  getUser(username: String): Observable<User> {
    return this.http
      .get<User>(`${this.usersUrl}/username/${username}`, this.httpOptions)
      .pipe(catchError(this.handleError<User>('getUser')));
  }

  getUserByID(id: Number): Observable<User> {
    return this.http
      .get<User>(`${this.usersUrl}/${id}`)
      .pipe(catchError(this.handleError<User>('getUserByID')));
  }

  getUsers(): Observable<User[]> {
    return this.http
      .get<User[]>(`${this.usersUrl}`)
      .pipe(catchError(this.handleError<User[]>('getUsers')));
  }

  /** PUT: update a user's info in the sterver */
  updateUser(user: User): Observable<User> {
    return this.http
      .put<User>(`${this.usersUrl}/${user.id}`, user, this.httpOptions)
      .pipe(catchError(this.handleError<User>('updateUser')));
  }

  /** GET: get a user's basket */
  getBasket(id: number): Observable<BasketNeed[]> {
    console.log(id);
    return this.http
      .get<BasketNeed[]>(`${this.usersUrl}/${id}/basket`, this.httpOptions)
      .pipe(catchError(this.handleError<BasketNeed[]>('getBasket')));
  }

  /** DELETE: delete a need from a user's basket */
  removeNeed(id: number, need: Need): Observable<User> {
    console.log(id, need);
    return this.http
      .delete<User>(`${this.usersUrl}/${id}/basket`, {
        headers: this.httpOptions.headers,
        body: need,
      })
      .pipe(catchError(this.handleError<User>('removeNeed')));
  }

  editCount(user: User, need: Need, count: number): Observable<User> {
    const url = `${this.usersUrl}/${user.id}/basket/${count}`;
    return this.http
      .put<User>(url, need, this.httpOptions)
      .pipe(catchError(this.handleError<User>('editCount')));
  }

  getCurrentUser(): Observable<User> {
    return of(this.currentUser);
  }
}
