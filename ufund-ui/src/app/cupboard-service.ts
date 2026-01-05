import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Need } from './need';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class NeedService {
  constructor(private http: HttpClient) {}
  needs: Need[] = [];
  private cupboardUrl = 'http://localhost:8080/cupboard';

  getNeeds(): Observable<Need[]> {
    return this.http
      .get<Need[]>(this.cupboardUrl)
      .pipe(catchError(this.handleError<Need[]>('getNeeds', [])));
  }

  getNeed(id: number): Observable<Need> {
    const url = `${this.cupboardUrl}/${id}`;
    return this.http.get<Need>(url).pipe(catchError(this.handleError<Need>(`getNeed id=${id}`)));
  }

  getNeedbyName(name: string): Observable<Need[]> {
    const url = `${this.cupboardUrl}/name/${name}`;
    return this.http
      .get<Need[]>(url)
      .pipe(catchError(this.handleError<Need[]>(`getNeedByName name=${name}`, [])));
  }

  updateNeed(need: Need): Observable<any> {
    const id = need.id;
    const url = `${this.cupboardUrl}/${id}`;
    return this.http.put<Need>(url, need, this.httpOptions).pipe(
      tap(() => console.log('Updated need:', { id })),
      catchError(this.handleError<Need>(`updateNeed id=${id}`))
    );
  }

  addNeed(need: Need): Observable<Need> {
    return this.http
      .post<Need>(this.cupboardUrl, need, this.httpOptions)
      .pipe(catchError(this.handleError<Need>('addNeed')));
  }

  deleteNeed(id: Number): Observable<Need> {
    const url = `${this.cupboardUrl}/${id}`;
    return this.http.delete<Need>(url).pipe(catchError(this.handleError<Need>('deleteNeed')));
  }

  getCurrentNeeds(): Observable<Need[]> {
    return of(this.needs);
  }

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      return of(result as T);
    };
  }
}
