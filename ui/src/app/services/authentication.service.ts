import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from '../../../node_modules/rxjs';

@Injectable()
export class AuthenticationService {

  constructor(private http: Http) {
  }

  public authenticated(): Observable<boolean> {
    return this.http.get(`rest/authenticated`)
      .map((response: Response) => {
        return response.ok;
      })
      .catch(() => {
        return Observable.of(false)
      });
  }
}
