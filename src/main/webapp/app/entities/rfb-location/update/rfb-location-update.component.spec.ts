jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RfbLocationService } from '../service/rfb-location.service';
import { IRfbLocation, RfbLocation } from '../rfb-location.model';

import { RfbLocationUpdateComponent } from './rfb-location-update.component';

describe('Component Tests', () => {
  describe('RfbLocation Management Update Component', () => {
    let comp: RfbLocationUpdateComponent;
    let fixture: ComponentFixture<RfbLocationUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let rfbLocationService: RfbLocationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RfbLocationUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RfbLocationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RfbLocationUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      rfbLocationService = TestBed.inject(RfbLocationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const rfbLocation: IRfbLocation = { id: 456 };

        activatedRoute.data = of({ rfbLocation });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(rfbLocation));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbLocation = { id: 123 };
        spyOn(rfbLocationService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbLocation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbLocation }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(rfbLocationService.update).toHaveBeenCalledWith(rfbLocation);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbLocation = new RfbLocation();
        spyOn(rfbLocationService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbLocation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbLocation }));
        saveSubject.complete();

        // THEN
        expect(rfbLocationService.create).toHaveBeenCalledWith(rfbLocation);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbLocation = { id: 123 };
        spyOn(rfbLocationService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbLocation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(rfbLocationService.update).toHaveBeenCalledWith(rfbLocation);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
