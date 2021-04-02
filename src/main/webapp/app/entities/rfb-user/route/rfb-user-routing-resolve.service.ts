import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRfbUser, RfbUser } from '../rfb-user.model';
import { RfbUserService } from '../service/rfb-user.service';

@Injectable({ providedIn: 'root' })
export class RfbUserRoutingResolveService implements Resolve<IRfbUser> {
  constructor(protected service: RfbUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRfbUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((rfbUser: HttpResponse<RfbUser>) => {
          if (rfbUser.body) {
            return of(rfbUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RfbUser());
  }
}
