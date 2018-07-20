import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AlertService} from '../../services/alert.service';
import {AuthenticationService} from '../../services/authentication.service';

@Injectable()
export class AuthenticationGuard implements CanActivate {
  constructor(private router: Router, private authenticationService: AuthenticationService, private alertService: AlertService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authenticationService.authenticated().map(x => {
      if (!x) {
        this.alertService.error("Please log-in first", true);
      }
      return x;
    })
  }

}
