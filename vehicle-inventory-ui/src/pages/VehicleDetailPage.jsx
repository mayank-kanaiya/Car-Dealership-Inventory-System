import { useParams, useNavigate } from 'react-router-dom';
import { useVehicleDetail } from '../features/vehicles/hooks/useVehicles';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import Badge from '../components/Badge/Badge';
import Button from '../components/Button/Button';
import { VEHICLE_CATEGORIES } from '../constants/vehicleCategories';

function VehicleDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data: vehicle, isLoading, error } = useVehicleDetail(id);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay message={error.message} />;
  }

  if (!vehicle) return null;

  const categoryLabel =
    VEHICLE_CATEGORIES.find((c) => c.value === vehicle.category)?.label || vehicle.category;

  return (
    <div>
      <button
        type="button"
        onClick={() => navigate(-1)}
        className="mb-6 text-sm text-primary hover:text-primary-hover transition-colors cursor-pointer"
      >
        &larr; Back to list
      </button>
      <div className="bg-surface rounded-xl border border-border overflow-hidden">
        <div className="md:flex">
          <div className="md:w-1/2 aspect-video md:aspect-auto bg-gray-100">
            <img
              src={vehicle.imageUrl || '/images/default-vehicle.svg'}
              alt={`${vehicle.make} ${vehicle.model}`}
              className="w-full h-full object-cover"
              onError={(e) => {
                e.target.src = '/images/default-vehicle.svg';
              }}
            />
          </div>
          <div className="p-6 md:w-1/2 flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-4">
              <h1 className="text-3xl font-bold text-text-primary">
                {vehicle.make} {vehicle.model}
              </h1>
              <Badge variant="info">{categoryLabel}</Badge>
            </div>
            <div className="text-4xl font-bold text-primary mb-6">
              ${vehicle.price.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </div>
            <div className="mb-6">
              <span
                className={`text-lg font-semibold ${vehicle.quantityInStock > 0 ? 'text-success' : 'text-danger'}`}
              >
                {vehicle.quantityInStock > 0
                  ? `${vehicle.quantityInStock} units in stock`
                  : 'Out of stock'}
              </span>
            </div>
            <div className="flex gap-3">
              <Button variant="primary" onClick={() => navigate('/vehicles')}>
                Browse Inventory
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default VehicleDetailPage;
