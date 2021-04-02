jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IRfbEvent, RfbEvent } from '../rfb-event.model';
import { RfbEventService } from '../service/rfb-event.service';

import { RfbEventRoutingResolveService } from './rfb-event-routing-resolve.service';

describe('Service Tests', () => {
  describe('RfbEvent routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: RfbEventRoutingResolveService;
    let service: RfbEventService;
    let resultRfbEvent: IRfbEvent | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(RfbEventRoutingResolveService);
      service = TestBed.inject(RfbEventService);
      resultRfbEvent = undefined;
    });

    describe('resolve', () => {
      it('should return IRfbEvent returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEvent = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbEvent).toEqual({ id: 123 });
      });

      it('should return new IRfbEvent if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEvent = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultRfbEvent).toEqual(new RfbEvent());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbEvent = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbEvent).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
