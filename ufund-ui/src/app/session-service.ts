import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Session } from './session';

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  private currentSession: Session | null = null;

  constructor(private http: HttpClient) {}

  private authUrl = 'http://localhost:8080/auth'; // URL to web api

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

  login(username: string, password: string): Observable<Session> {
    const url = `${this.authUrl}/login`;
    return this.http
      .post<Session>(url, { username, password })
      .pipe(catchError(this.handleError<Session>('login')));
  }

  loginHash(username: string, password: string): Observable<Session> {
    const url = `${this.authUrl}/login/hash`;
    return this.http
      .post<Session>(url, { username, password })
      .pipe(catchError(this.handleError<Session>('loginHash')));
  }

  /** GET session by username. Will 404 if id not found */
  getSession(username: String): Observable<Session> {
    const url = `${this.authUrl}/${username}`;
    return this.http
      .get<Session>(url)
      .pipe(catchError(this.handleError<Session>(`getSession id=${username}`)));
  }

  isValidSession(username: String): Observable<boolean> {
    const url = `${this.authUrl}/${username}`;
    return this.http
      .get<boolean>(url)
      .pipe(catchError(this.handleError<boolean>(`isValidSession username=${username}`)));
  }

  /** PUT: update the session on the server */
  validateSession(username: String): Observable<any> {
    const url = `${this.authUrl}/${username}`;
    return this.http
      .put<Session>(url, null)
      .pipe(catchError(this.handleError<any>('validateSession')));
  }

  /** DELETE: delete the session from the server */
  deleteSession(username: String): Observable<Session> {
    const url = `${this.authUrl}/${username}`;
    return this.http
      .delete<Session>(url)
      .pipe(catchError(this.handleError<Session>('deleteSession')));
  }
}
