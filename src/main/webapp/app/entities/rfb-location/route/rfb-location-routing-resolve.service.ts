import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRfbLocation, RfbLocation } from '../rfb-location.model';
import { RfbLocationService } from '../service/rfb-location.service';

@Injectable({ providedIn: 'root' })
export class RfbLocationRoutingResolveService implements Resolve<IRfbLocation> {
  constructor(protected service: RfbLocationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRfbLocation> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((rfbLocation: HttpResponse<RfbLocation>) => {
          if (rfbLocation.body) {
            return of(rfbLocation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RfbLocation());
  }
}
