import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRfbEvent, RfbEvent } from '../rfb-event.model';
import { RfbEventService } from '../service/rfb-event.service';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

@Component({
  selector: 'jhi-rfb-event-update',
  templateUrl: './rfb-event-update.component.html',
})
export class RfbEventUpdateComponent implements OnInit {
  isSaving = false;

  rfbLocationsSharedCollection: IRfbLocation[] = [];

  editForm = this.fb.group({
    id: [],
    eventDate: [],
    eventCode: [],
    rfbLocation: [],
  });

  constructor(
    protected rfbEventService: RfbEventService,
    protected rfbLocationService: RfbLocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbEvent }) => {
      this.updateForm(rfbEvent);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rfbEvent = this.createFromForm();
    if (rfbEvent.id !== undefined) {
      this.subscribeToSaveResponse(this.rfbEventService.update(rfbEvent));
    } else {
      this.subscribeToSaveResponse(this.rfbEventService.create(rfbEvent));
    }
  }

  trackRfbLocationById(index: number, item: IRfbLocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRfbEvent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(rfbEvent: IRfbEvent): void {
    this.editForm.patchValue({
      id: rfbEvent.id,
      eventDate: rfbEvent.eventDate,
      eventCode: rfbEvent.eventCode,
      rfbLocation: rfbEvent.rfbLocation,
    });

    this.rfbLocationsSharedCollection = this.rfbLocationService.addRfbLocationToCollectionIfMissing(
      this.rfbLocationsSharedCollection,
      rfbEvent.rfbLocation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.rfbLocationService
      .query()
      .pipe(map((res: HttpResponse<IRfbLocation[]>) => res.body ?? []))
      .pipe(
        map((rfbLocations: IRfbLocation[]) =>
          this.rfbLocationService.addRfbLocationToCollectionIfMissing(rfbLocations, this.editForm.get('rfbLocation')!.value)
        )
      )
      .subscribe((rfbLocations: IRfbLocation[]) => (this.rfbLocationsSharedCollection = rfbLocations));
  }

  protected createFromForm(): IRfbEvent {
    return {
      ...new RfbEvent(),
      id: this.editForm.get(['id'])!.value,
      eventDate: this.editForm.get(['eventDate'])!.value,
      eventCode: this.editForm.get(['eventCode'])!.value,
      rfbLocation: this.editForm.get(['rfbLocation'])!.value,
    };
  }
}
