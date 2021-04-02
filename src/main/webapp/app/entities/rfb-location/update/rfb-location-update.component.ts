import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRfbLocation, RfbLocation } from '../rfb-location.model';
import { RfbLocationService } from '../service/rfb-location.service';

@Component({
  selector: 'jhi-rfb-location-update',
  templateUrl: './rfb-location-update.component.html',
})
export class RfbLocationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    locationName: [],
    runDayOfWeek: [],
  });

  constructor(protected rfbLocationService: RfbLocationService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbLocation }) => {
      this.updateForm(rfbLocation);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rfbLocation = this.createFromForm();
    if (rfbLocation.id !== undefined) {
      this.subscribeToSaveResponse(this.rfbLocationService.update(rfbLocation));
    } else {
      this.subscribeToSaveResponse(this.rfbLocationService.create(rfbLocation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRfbLocation>>): void {
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

  protected updateForm(rfbLocation: IRfbLocation): void {
    this.editForm.patchValue({
      id: rfbLocation.id,
      locationName: rfbLocation.locationName,
      runDayOfWeek: rfbLocation.runDayOfWeek,
    });
  }

  protected createFromForm(): IRfbLocation {
    return {
      ...new RfbLocation(),
      id: this.editForm.get(['id'])!.value,
      locationName: this.editForm.get(['locationName'])!.value,
      runDayOfWeek: this.editForm.get(['runDayOfWeek'])!.value,
    };
  }
}
