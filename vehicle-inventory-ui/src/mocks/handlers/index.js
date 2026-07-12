import { authHandlers } from './authHandlers';
import { vehicleHandlers } from './vehicleHandlers';

export const handlers = [...authHandlers, ...vehicleHandlers];
