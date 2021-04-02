import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRfbEventAttendance, RfbEventAttendance } from '../rfb-event-attendance.model';
import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';

@Injectable({ providedIn: 'root' })
export class RfbEventAttendanceRoutingResolveService implements Resolve<IRfbEventAttendance> {
  constructor(protected service: RfbEventAttendanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRfbEventAttendance> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((rfbEventAttendance: HttpResponse<RfbEventAttendance>) => {
          if (rfbEventAttendance.body) {
            return of(rfbEventAttendance.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RfbEventAttendance());
  }
}
