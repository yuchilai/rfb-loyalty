jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IRfbEventAttendance, RfbEventAttendance } from '../rfb-event-attendance.model';
import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';

import { RfbEventAttendanceRoutingResolveService } from './rfb-event-attendance-routing-resolve.service';

describe('Service Tests', () => {
  describe('RfbEventAttendance routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: RfbEventAttendanceRoutingResolveService;
    let service: RfbEventAttendanceService;
    let resultRfbEventAttendance: IRfbEventAttendance | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(RfbEventAttendanceRoutingResolveService);
      service = TestBed.inject(RfbEventAttendanceService);
      resultRfbEventAttendance = undefined;
    });

    describe('resolve', () => {
      it('should return IRfbEventAttendance returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEventAttendance = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbEventAttendance).toEqual({ id: 123 });
      });

      it('should return new IRfbEventAttendance if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEventAttendance = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultRfbEventAttendance).toEqual(new RfbEventAttendance());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEventAttendance = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbEventAttendance).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
