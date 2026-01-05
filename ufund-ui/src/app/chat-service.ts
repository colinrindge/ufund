import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { ChatPersonality } from './ChatPersonality';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  constructor(private http: HttpClient) {}

  private chatUrl = 'http://localhost:8080/chat';
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  registerChat(id: number, personality: ChatPersonality) {
    const url = `${this.chatUrl}/${id}`;
    console.log(personality);
    return this.http
      .post<number>(url, personality, this.httpOptions)
      .pipe(catchError(this.handleError<number>('registerChat')));
  }

  sendChat(id: number, message: string) {
    const url = `${this.chatUrl}/${id}`;
    return this.http
      .put<string>(url, { message })
      .pipe(catchError(this.handleError<string>('sendChat')));
  }

  chatExists(id: number) {
    const url = `${this.chatUrl}/${id}`;
    return this.http.get<boolean>(url).pipe(catchError(this.handleError<boolean>('chatExists')));
  }

  getPersonalities(): Observable<ChatPersonality[]> {
    const url = `${this.chatUrl}/personalities`;
    return this.http
      .get<ChatPersonality[]>(url)
      .pipe(catchError(this.handleError<ChatPersonality[]>('getPersonalities()')));
  }

  deleteChat(id: number) {
    const url = `${this.chatUrl}/${id}`;
    return this.http.delete<Boolean>(url).pipe(catchError(this.handleError<string>('deleteChat')));
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      return of(result as T);
    };
  }
}
