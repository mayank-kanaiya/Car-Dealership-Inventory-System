import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit } from 'lucide-react';
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
        className="flex items-center gap-1.5 mb-6 text-sm font-medium text-text-secondary hover:text-primary transition-colors cursor-pointer"
      >
        <ArrowLeft size={16} />
        Back to inventory
      </button>

      <div className="bg-surface rounded-2xl border border-border overflow-hidden">
        <div className="md:flex">
          <div className="md:w-1/2 bg-gradient-to-br from-surface-secondary to-gray-200 dark:to-gray-800">
            <img
              src={vehicle.imageUrl || '/images/default-vehicle.svg'}
              alt={`${vehicle.make} ${vehicle.model}`}
              className="w-full h-full object-cover min-h-[300px]"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          </div>
          <div className="p-6 sm:p-8 md:w-1/2 flex flex-col justify-center">
            <div className="flex items-center gap-3 mb-2">
              <Badge variant="info">{categoryLabel}</Badge>
            </div>
            <h1 className="text-3xl sm:text-4xl font-bold text-text-primary mt-3">
              {vehicle.make} {vehicle.model}
            </h1>
            <div className="text-3xl sm:text-4xl font-bold text-primary mt-4">
              ${vehicle.price.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </div>

            <div className="mt-6 flex items-center gap-2">
              <span
                className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium ${
                  vehicle.quantityInStock > 0
                    ? 'bg-green-50 text-success dark:bg-green-950/50'
                    : 'bg-red-50 text-danger dark:bg-red-950/50'
                }`}
              >
                <span
                  className={`w-2 h-2 rounded-full ${vehicle.quantityInStock > 0 ? 'bg-success' : 'bg-danger'}`}
                />
                {vehicle.quantityInStock > 0
                  ? `${vehicle.quantityInStock} units in stock`
                  : 'Out of stock'}
              </span>
            </div>

            <div className="flex gap-3 mt-8">
              <Button
                variant="primary"
                icon={<Edit size={16} />}
                onClick={() => navigate(`/vehicles/${vehicle.id}/edit`)}
              >
                Edit Vehicle
              </Button>
              <Button variant="secondary" onClick={() => navigate('/vehicles')}>
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
