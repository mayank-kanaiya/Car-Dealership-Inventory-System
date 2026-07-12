import { useState } from 'react';
import { useVehicleList } from '../features/vehicles/hooks/useVehicles';
import VehicleCard from '../features/vehicles/components/VehicleCard';
import VehicleFilters from '../features/vehicles/components/VehicleFilters';
import Pagination from '../components/Pagination/Pagination';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import EmptyState from '../components/EmptyState/EmptyState';
import { Car } from 'lucide-react';

function VehicleListPage() {
  const [filters, setFilters] = useState({ page: 0, size: 12 });
  const { data, isLoading, error } = useVehicleList(filters);

  const handleFilter = (newFilters) => {
    setFilters((prev) => ({ ...prev, ...newFilters, page: 0 }));
  };

  const handlePageChange = (page) => {
    setFilters((prev) => ({ ...prev, page: page - 1 }));
  };

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

  const vehicles = data?.content || [];
  const totalPages = data?.totalPages || 0;
  const totalElements = data?.totalElements || 0;

  return (
    <div>
      <div className="mb-6">
        <div className="flex items-center gap-3 mb-1">
          <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-primary/10">
            <Car size={20} className="text-primary" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-text-primary">Vehicle Inventory</h1>
            <p className="text-sm text-text-secondary">
              {totalElements} {totalElements === 1 ? 'vehicle' : 'vehicles'} available
            </p>
          </div>
        </div>
      </div>

      <div className="bg-surface rounded-2xl border border-border p-4 sm:p-6 mb-6">
        <VehicleFilters onFilter={handleFilter} />
      </div>

      {vehicles.length === 0 ? (
        <EmptyState
          title="No vehicles found"
          description="Try adjusting your search filters or add a new vehicle to get started."
          icon={<Car size={48} strokeWidth={1.5} />}
        />
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5 sm:gap-6">
            {vehicles.map((vehicle) => (
              <VehicleCard key={vehicle.id} vehicle={vehicle} />
            ))}
          </div>
          {totalPages > 1 && (
            <div className="mt-8">
              <Pagination
                currentPage={filters.page + 1}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default VehicleListPage;
