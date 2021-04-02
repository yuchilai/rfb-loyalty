jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RfbEventService } from '../service/rfb-event.service';
import { IRfbEvent, RfbEvent } from '../rfb-event.model';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

import { RfbEventUpdateComponent } from './rfb-event-update.component';

describe('Component Tests', () => {
  describe('RfbEvent Management Update Component', () => {
    let comp: RfbEventUpdateComponent;
    let fixture: ComponentFixture<RfbEventUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let rfbEventService: RfbEventService;
    let rfbLocationService: RfbLocationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RfbEventUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RfbEventUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RfbEventUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      rfbEventService = TestBed.inject(RfbEventService);
      rfbLocationService = TestBed.inject(RfbLocationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call RfbLocation query and add missing value', () => {
        const rfbEvent: IRfbEvent = { id: 456 };
        const rfbLocation: IRfbLocation = { id: 84547 };
        rfbEvent.rfbLocation = rfbLocation;

        const rfbLocationCollection: IRfbLocation[] = [{ id: 85861 }];
        spyOn(rfbLocationService, 'query').and.returnValue(of(new HttpResponse({ body: rfbLocationCollection })));
        const additionalRfbLocations = [rfbLocation];
        const expectedCollection: IRfbLocation[] = [...additionalRfbLocations, ...rfbLocationCollection];
        spyOn(rfbLocationService, 'addRfbLocationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ rfbEvent });
        comp.ngOnInit();

        expect(rfbLocationService.query).toHaveBeenCalled();
        expect(rfbLocationService.addRfbLocationToCollectionIfMissing).toHaveBeenCalledWith(
          rfbLocationCollection,
          ...additionalRfbLocations
        );
        expect(comp.rfbLocationsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const rfbEvent: IRfbEvent = { id: 456 };
        const rfbLocation: IRfbLocation = { id: 133 };
        rfbEvent.rfbLocation = rfbLocation;

        activatedRoute.data = of({ rfbEvent });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(rfbEvent));
        expect(comp.rfbLocationsSharedCollection).toContain(rfbLocation);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEvent = { id: 123 };
        spyOn(rfbEventService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEvent });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbEvent }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(rfbEventService.update).toHaveBeenCalledWith(rfbEvent);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEvent = new RfbEvent();
        spyOn(rfbEventService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEvent });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbEvent }));
        saveSubject.complete();

        // THEN
        expect(rfbEventService.create).toHaveBeenCalledWith(rfbEvent);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEvent = { id: 123 };
        spyOn(rfbEventService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEvent });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(rfbEventService.update).toHaveBeenCalledWith(rfbEvent);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRfbLocationById', () => {
        it('Should return tracked RfbLocation primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRfbLocationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
