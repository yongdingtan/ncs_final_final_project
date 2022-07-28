import { TestBed } from '@angular/core/testing';

import { HelloWordService } from './hello-world.service';

describe('HelloWordService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: HelloWordService = TestBed.get(HelloWordService);
    expect(service).toBeTruthy();
  });
});