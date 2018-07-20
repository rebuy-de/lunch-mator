import {Injectable} from '@angular/core';
import {Headers, Http, Response} from '@angular/http';
import {Observable} from 'rxjs';
import 'rxjs/Rx';
import {CommentDto, NewCommentDto} from '../types';

@Injectable()
export class CommentService {
  private messageUrl = '/rest/comments';

  constructor(private http: Http) {
  }

  public getMessagesForLunch(lunchId: number): Observable<CommentDto[]> {
    return this.http.get(`${this.messageUrl}/${lunchId}`).map((response: Response) => {
      return response.json();
    });
  }

  public postMessage(lunchId: number, comment: NewCommentDto): Observable<Response> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.post(`${this.messageUrl}/${lunchId}`, comment, {headers});
  }
}
